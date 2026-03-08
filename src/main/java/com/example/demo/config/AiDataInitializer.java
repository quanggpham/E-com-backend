package com.example.demo.config;

import com.example.demo.service.AiProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.ai.init-data", havingValue = "true")
public class AiDataInitializer implements CommandLineRunner {

    private final AiProductService aiProductService;

    @Override
    public void run(String... args) {
        aiProductService.indexingMenuToAi();
    }
}