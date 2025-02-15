package com.printerserver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")  // 🔹 Ruta principal (root)
    public String helloWorld() {
        return "¡Hello World desde Spring Boot!";
    }
}