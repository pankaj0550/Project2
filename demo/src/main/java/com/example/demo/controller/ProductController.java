package com.example.demo.controller;



import com.example.demo.model.Product;
import com.example.demo.repository.InventoryTransactionRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryTransactionRepository transactionRepository;


    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products/list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "products/add";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "products/add";
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "products/edit";
        }
        return "redirect:/products";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "products/edit";
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        try {
            // Remove all transactions for this product
            transactionRepository.deleteByProductId(id);
            productRepository.deleteById(id);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to delete product: " + e.getMessage());
            return "products/error";
        }
        return "redirect:/products";
    }

}
