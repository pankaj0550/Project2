package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("home")
public class HomeController {

    @GetMapping
    public String home() {
        return "home"; // This will render home.html
    }

    @GetMapping("/products")
    public String viewProducts() {
        return "products";
    }

    @GetMapping("/suppliers")
    public String viewSuppliers() {
        return "suppliers";
    }
}
