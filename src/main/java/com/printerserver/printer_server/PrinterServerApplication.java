package com.printerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrinterServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrinterServerApplication.class, args);
        System.out.println("Running app - Printer Server");
    }
}