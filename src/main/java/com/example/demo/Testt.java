package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testt {
    @GetMapping("/hi")
    public String hello() {
        return "con cac";
    }
}
