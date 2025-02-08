package ru.yandex.practicum;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AnalyzerApp {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AnalyzerApp.class, args);
    }
}
