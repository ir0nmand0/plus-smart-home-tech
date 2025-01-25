package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.warehouse.client",
        "ru.yandex.practicum.store.client"
})
public class ShoppingCartApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }
}