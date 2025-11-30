package com.restaurant.order.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> testPost(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Teste funcionando!");
        response.put("received", data);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> testGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Teste GET funcionando!");
        return ResponseEntity.ok(response);
    }
}
