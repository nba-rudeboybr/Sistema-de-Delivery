package com.ibeus.Comanda.Digital.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple-orders")
@CrossOrigin(origins = "http://localhost:4200")
public class SimpleOrderController {
    
    @PostMapping
    public String createSimpleOrder(@RequestBody String orderData) {
        return "Order created: " + orderData;
    }
    
    @GetMapping
    public String getSimpleOrders() {
        return "Simple orders working";
    }
}

