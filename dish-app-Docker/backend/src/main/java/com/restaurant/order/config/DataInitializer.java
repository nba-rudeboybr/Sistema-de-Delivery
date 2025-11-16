package com.restaurant.order.config;

import com.restaurant.order.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private DishService dishService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Inicializando dados padrão...");
        dishService.initializeDefaultDishes();
        System.out.println("Dados padrão inicializados com sucesso!");
    }
}
