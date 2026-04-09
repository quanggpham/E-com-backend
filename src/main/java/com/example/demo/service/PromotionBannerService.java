package com.example.demo.service;

import com.example.demo.dto.request.PromotionBannerReorderRequest;
import com.example.demo.dto.request.PromotionBannerRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.PromotionBannerResponse;
import com.example.demo.entity.PromotionBanner;
import com.example.demo.exception.BaseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PromotionBannerMapper;
import com.example.demo.repository.PromotionBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PromotionBannerService {

    private final PromotionBannerRepository promotionBannerRepository;
    private final PromotionBannerMapper promotionBannerMapper;

    @Transactional(readOnly = true)
    public List<PromotionBannerResponse> getActivePromotionBanners() {
        return promotionBannerRepository.findActivePromotionBanners(LocalDateTime.now())
                .stream()
                .map(promotionBannerMapper::toPromotionBannerResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PromotionBannerResponse> getAllPromotionBanners(Pageable pageable) {
        int validPageSize = Math.min(pageable.getPageSize(), 50);
        Pageable normalizedPageable = PageRequest.of(pageable.getPageNumber(), validPageSize, pageable.getSort());

        Page<PromotionBanner> pageData = promotionBannerRepository.findAllByOrderByDisplayOrderAscIdAsc(normalizedPageable);

        return PageResponse.<PromotionBannerResponse>builder()
                .items(pageData.getContent().stream().map(promotionBannerMapper::toPromotionBannerResponse).toList())
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageData.getNumber() + 1)
                .build();
    }

    @Transactional(readOnly = true)
    public PromotionBannerResponse getPromotionBannerById(Long id) {
        return promotionBannerMapper.toPromotionBannerResponse(findPromotionBanner(id));
    }

    @Transactional
    public PromotionBannerResponse createPromotionBanner(PromotionBannerRequest request) {
        validateSchedule(request);
        PromotionBanner promotionBanner = promotionBannerMapper.toPromotionBanner(request);
        normalizeDefaults(promotionBanner);
        return promotionBannerMapper.toPromotionBannerResponse(promotionBannerRepository.save(promotionBanner));
    }

    @Transactional
    public PromotionBannerResponse updatePromotionBanner(Long id, PromotionBannerRequest request) {
        validateSchedule(request);
        PromotionBanner promotionBanner = findPromotionBanner(id);
        promotionBannerMapper.updatePromotionBannerFromRequest(request, promotionBanner);
        normalizeDefaults(promotionBanner);
        return promotionBannerMapper.toPromotionBannerResponse(promotionBannerRepository.save(promotionBanner));
    }

    @Transactional
    public void deletePromotionBanner(Long id) {
        promotionBannerRepository.delete(findPromotionBanner(id));
    }

    @Transactional
    public void changePromotionBannerStatus(Long id, boolean active) {
        PromotionBanner promotionBanner = findPromotionBanner(id);
        promotionBanner.setActive(active);
        promotionBannerRepository.save(promotionBanner);
    }

    @Transactional
    public List<PromotionBannerResponse> reorderPromotionBanners(List<PromotionBannerReorderRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BaseException("Danh sách sắp xếp promotion banner không được để trống");
        }

        Set<Long> uniqueIds = requests.stream()
                .map(PromotionBannerReorderRequest::getId)
                .collect(java.util.stream.Collectors.toSet());

        if (uniqueIds.size() != requests.size()) {
            throw new BaseException("Danh sách sắp xếp promotion banner có id bị trùng");
        }

        Map<Long, PromotionBanner> bannersById = new LinkedHashMap<>();
        promotionBannerRepository.findAllById(uniqueIds)
                .forEach(banner -> bannersById.put(banner.getId(), banner));

        if (bannersById.size() != requests.size()) {
            Long missingId = requests.stream()
                    .map(PromotionBannerReorderRequest::getId)
                    .filter(id -> !bannersById.containsKey(id))
                    .findFirst()
                    .orElse(null);
            throw new ResourceNotFoundException("PromotionBanner", "id", missingId);
        }

        for (PromotionBannerReorderRequest request : requests) {
            bannersById.get(request.getId()).setDisplayOrder(request.getDisplayOrder());
        }

        promotionBannerRepository.saveAll(bannersById.values());

        return requests.stream()
                .map(PromotionBannerReorderRequest::getId)
                .map(bannersById::get)
                .map(promotionBannerMapper::toPromotionBannerResponse)
                .toList();
    }

    private PromotionBanner findPromotionBanner(Long id) {
        return promotionBannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromotionBanner", "id", id));
    }

    private void validateSchedule(PromotionBannerRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BaseException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
    }

    private void normalizeDefaults(PromotionBanner promotionBanner) {
        if (promotionBanner.getDisplayOrder() == null) {
            promotionBanner.setDisplayOrder(0);
        }
        if (promotionBanner.getActive() == null) {
            promotionBanner.setActive(true);
        }
    }
}
