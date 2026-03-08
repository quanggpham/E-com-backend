package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodAdvisorService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final int MAX_SEARCH_RESULTS = 4;
    private static final double SIMILARITY_THRESHOLD = 0.3;

    public Flux<String> advise(String question) {
        log.info("Question: {}", question);


        List<Document> similarDocuments = searchSimilarDocuments(question);

        log.info("Found {} documents from vector search", similarDocuments.size());

        if (similarDocuments.isEmpty()) {
            log.warn("No documents found! Check Pinecone connection or data");
            return Flux.just("Không tìm thấy thông tin món ăn. Vui lòng thử câu hỏi khác.");
        }


        for (int i = 0; i < similarDocuments.size(); i++) {
            Document doc = similarDocuments.get(i);
            log.info("Doc {}: {}...", i + 1,
                    doc.getText().substring(0, Math.min(100, doc.getText().length())));
        }


        String context = buildContext(similarDocuments);
        log.info("Context length: {} characters", context.length());

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text("""
                    HƯỚNG DẪN CỦA HỆ THỐNG:
                    Bạn là nhân viên bán hàng của một nhà hàng Việt Nam và là chuyên gia tư vấn ẩm thực của quán.
                    Sử dụng thông tin từ menu được cung cấp để tư vấn món ăn phù hợp cho khách hàng.
                    Gợi ý ÍT NHẤT 3 món ăn phù hợp.
                    Với mỗi món, nêu tên, giá (nếu có), và lý do phù hợp.
                    Luôn trả lời bằng tiếng Việt, thái độ thân thiện.
                    TUYỆT ĐỐI KHÔNG bịa ra món ăn không có trong menu.
                    
                    ---
                    THÔNG TIN MENU:
                    {context}
                    
                    CÂU HỎI CỦA KHÁCH: 
                    {question}
                    """)
                        .param("context", context)
                        .param("question", question)
                )
                .stream()
                .content();
    }

    private List<Document> searchSimilarDocuments(String query) {
        try {
            log.info("Searching vector store for: {}", query);

            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(MAX_SEARCH_RESULTS)
                    .similarityThreshold(SIMILARITY_THRESHOLD)
                    .build();

            List<Document> results = vectorStore.similaritySearch(searchRequest);

            log.info("Vector search returned {} results", results.size());

            return results;

        } catch (Exception e) {
            log.error("Error searching vector store: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append(String.format("%d. %s\n", i + 1, doc.getText()));
        }
        return context.toString();
    }
}