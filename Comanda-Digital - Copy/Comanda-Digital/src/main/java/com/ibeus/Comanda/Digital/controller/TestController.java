package com.ibeus.Comanda.Digital.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {
    
    @PostMapping
    public String testPost(@RequestBody String body) {
        return "Received: " + body;
    }
    
    @GetMapping
    public String testGet() {
        return "Test GET working";
    }
}

