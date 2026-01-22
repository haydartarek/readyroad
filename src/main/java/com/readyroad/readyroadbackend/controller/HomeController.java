// src/main/java/com/readyroad/readyroadbackend/controller/HomeController.java
package com.readyroad.readyroadbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "ReadyRoad API is running! ✅";
    }
}
