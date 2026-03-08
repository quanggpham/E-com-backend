package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiProductService {
    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    @Transactional
    public void indexingMenuToAi() {
        List<Product> products = productRepository.findAll();

        log.info("Bắt đầu mã hóa và nạp món ăn vào database");

        List<Document> documents = products.stream()
                .map(p -> {
                    String content = String.format("Danh mục: %s. Tên món: %s. Mô tả: %s. Giá: %s VNĐ.",
                            p.getCategory().getName(),
                            p.getName(),
                            p.getDescription(),
                            p.getPrice());

                    Map<String, Object> metadata = Map.of(
                            "productId", p.getId(),
                            "categoryName", p.getCategory().getName(),
                            "price", p.getPrice().doubleValue()
                    );

                    return new Document(content, metadata);
                })
                .toList();
        vectorStore.add(documents);
        log.info("Nạp tri thức xong");
    }
}
