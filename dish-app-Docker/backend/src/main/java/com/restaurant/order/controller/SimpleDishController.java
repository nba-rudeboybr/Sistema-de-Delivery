package com.restaurant.order.controller;

import com.restaurant.order.model.Dish;
import com.restaurant.order.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
@CrossOrigin(origins = "http://localhost:4200")
public class SimpleDishController {
    
    @Autowired
    private DishService dishService;
    
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }
    
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        Dish createdDish = dishService.createDish(dish);
        return ResponseEntity.ok(createdDish);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        Optional<Dish> dish = dishService.getDishById(id);
        if (dish.isPresent()) {
            return ResponseEntity.ok(dish.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        try {
            Dish updatedDish = dishService.updateDish(id, dish);
            return ResponseEntity.ok(updatedDish);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        try {
            dishService.deleteDish(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
