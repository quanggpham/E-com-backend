package com.example.demo.service;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductLike;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductLikeRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Transactional
    public boolean toggleLike(Long productId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        Optional<ProductLike> existing = productLikeRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            productLikeRepository.delete(existing.get());
            return false;
        } else {
            ProductLike like = ProductLike.builder()
                    .user(user)
                    .product(product)
                    .build();
            productLikeRepository.save(like);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long productId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        return productLikeRepository.existsByUserAndProduct(user, product);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        return productLikeRepository.countByProduct(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLikedProducts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        List<ProductLike> likes = productLikeRepository.findByUserOrderByCreatedAtDesc(user);
        return likes.stream()
                .map(pl -> {
                    ProductResponse response = productMapper.toResponse(pl.getProduct());
                    response.setLikeCount(productLikeRepository.countByProduct(pl.getProduct()));
                    response.setLiked(true);
                    return response;
                })
                .toList();
    }
}
