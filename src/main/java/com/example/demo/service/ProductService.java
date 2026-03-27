package com.example.demo.service;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.request.ProductSearchRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductLikeRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.specification.ProductSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public ProductResponse create(@Valid ProductCreationRequest request) {
        Product product = productMapper.toEntity(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        product.setCategory(category);
        product.setIsActive(true);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        productMapper.updateProductFromDto(request, product);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse findById(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        ProductResponse response = productMapper.toResponse(product);
        enrichWithLikeInfo(response, product, userId);
        return response;
    }

    public PageResponse<ProductResponse> search(ProductSearchRequest request, Pageable pageable, Long userId) {
        int validatedSize = Math.min(pageable.getPageSize(), 50);

        Pageable finalPageable = PageRequest.of(
                pageable.getPageNumber(),
                validatedSize,
                pageable.getSort()
        );

        Specification<Product> spec = Specification
                .allOf(ProductSpecification.hasCategory(request.getCategoryId())
                        .and(ProductSpecification.hasName(request.getName())
                                .and(ProductSpecification.hasPrice(request.getMinPrice(), request.getMaxPrice()))));

        Page<Product> pageData = productRepository.findAll(spec, finalPageable);
        List<ProductResponse> response = pageData.getContent().stream()
                .map(product -> {
                    ProductResponse productResponse = productMapper.toResponse(product);
                    enrichWithLikeInfo(productResponse, product, userId);
                    return productResponse;
                })
                .toList();

        return PageResponse.<ProductResponse>builder()
                .pageSize(pageData.getSize())
                .currentPage(pageData.getNumber() + 1)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .items(response)
                .build();
    }

    private void enrichWithLikeInfo(ProductResponse response, Product product, Long userId) {
        response.setLikeCount(productLikeRepository.countByProductId(product.getId()));
        response.setLiked(userId != null && productLikeRepository.existsByUserIdAndProductId(userId, product.getId()));
    }
}
