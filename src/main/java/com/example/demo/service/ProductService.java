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
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.specification.ProductSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse create(@Valid ProductCreationRequest request) {
        Product product = productMapper.toEntity(request);

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        product.setCategory(category);
        product.setIsActive(true);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        productMapper.updateProductFromDto(request, product);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.deleteById(id);
        return productMapper.toResponse(product);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponse(product);
    }

//    public PageResponse<ProductResponse> findAll(int page, int size, String sortBy, String sortDirection, String name) {
//        if (size > 51)
//            size = 50;
//        if (size < 1)
//            size = 1;
//        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
//                    Sort.by(Sort.Direction.ASC, sortBy) :
//                    Sort.by(Sort.Direction.DESC, sortBy);
//        Pageable pageable = PageRequest.of(page - 1, size, sort);
//
//        Page<Product> pageData;
//
//        if (name != null)
//        {
//            pageData = productRepository.findByNameContainingIgnoreCase(name, pageable);
//        }
//        else
//        {
//            pageData = productRepository.findAll(pageable);
//        }
//
//        List<ProductResponse> response = pageData.getContent().stream()
//                .map(productMapper::toResponse).collect(Collectors.toList());
//        return PageResponse.<ProductResponse>builder()
//                .currentPage(page)
//                .pageSize(pageData.getSize())
//                .totalPages(pageData.getTotalPages())
//                .totalElements(pageData.getTotalElements())
//                .items(response)
//                .build();
//    }

    public PageResponse<ProductResponse> search(ProductSearchRequest request) {
        if (request.getPage() < 1)
            request.setPage(1);
        if (request.getSize() > 51)
            request.setSize(50);

        Sort sort = request.getSortDirection().equalsIgnoreCase("asc") ?
                    Sort.by(Sort.Direction.ASC, request.getSortBy()) :
                Sort.by(Sort.Direction.DESC, request.getSortBy());

        Specification<Product> spec = Specification.allOf(ProductSpecification.hasCategory(request.getCategoryId()).and(ProductSpecification.hasName(request.getName()).and(ProductSpecification.hasPrice(request.getMinPrice(), request.getMaxPrice()))));

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        Page<Product> pageData = productRepository.findAll(spec, pageable);
        List<ProductResponse> response = pageData.getContent().stream()
                .map(productMapper::toResponse).collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .pageSize(request.getSize())
                .currentPage(request.getPage())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .items(response)
                .build();
    }
}
