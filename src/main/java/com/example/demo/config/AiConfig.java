package com.example.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
//                .defaultSystem("""
//                    Bạn là chuyên gia tư vấn ẩm thực của quán ăn.
//
//                    Nhiệm vụ:
//                    - Tư vấn món ăn phù hợp với sở thích và nhu cầu của khách hàng
//                    - Giới thiệu chi tiết về thành phần, giá cả, hương vị
//                    - Đề xuất combo hoặc món ăn kèm phù hợp
//                    - Trả lời các câu hỏi về dinh dưỡng, allergen
//
//                    Quy tắc:
//                    - Chỉ tư vấn các món có trong menu được cung cấp
//                    - Không bịa ra món ăn không tồn tại
//                    - Luôn trả lời bằng tiếng Việt
//                    - Thân thiện, chuyên nghiệp, ngắn gọn
//                    """)
                .build();
    }

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }
}