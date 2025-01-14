package com.example.demo.controller;

import com.example.demo.model.InventoryTransaction;
import com.example.demo.model.Product;
import com.example.demo.repository.InventoryTransactionRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public String showInventory(Model model) {
        model.addAttribute("transactions", transactionRepository.findAll());
        return "inventory/list";
    }

    @GetMapping("/add/{productId}")
    public String showAddStockForm(@PathVariable Long productId, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("transaction", new InventoryTransaction());
            return "inventory/add";
        }
        model.addAttribute("error", "Product not found.");
        return "inventory/error";
    }

    @PostMapping("/add/{productId}")
    public String add(@PathVariable Long productId, @RequestParam int quantity, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product p = product.get();

            // Update the product's quantity on hand
            p.setQuantityOnHand(p.getQuantityOnHand() + quantity);
            productRepository.save(p);

            // Create and save a transaction for the addition
            InventoryTransaction transaction = createInventoryTransaction(p, quantity, InventoryTransaction.TransactionType.INBOUND);
            transactionRepository.save(transaction);

            return "redirect:/inventory";
        }
        model.addAttribute("error", "Product not found.");
        return "inventory/error";
    }


    // Show the remove stock form (already working)
    @GetMapping("/remove/{productId}")
    public String showRemoveStockForm(@PathVariable Long productId, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "inventory/remove"; // Thymeleaf template: remove.html
        }
        model.addAttribute("error", "Product not found.");
        return "inventory/error";
    }

    // Handle stock removal (POST)
    @PostMapping("/remove/{productId}")
    public String removeStock(@PathVariable Long productId, @RequestParam int quantity, @RequestParam String suppliedTo,
                              @RequestParam double sellingPrice, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            if (quantity <= p.getQuantityOnHand()) {
                p.setQuantityOnHand(p.getQuantityOnHand() - quantity);
                productRepository.save(p);


                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setProduct(p);
                transaction.setQuantity(quantity);
                transaction.setSuppliedTo(suppliedTo);
                transaction.setSellingPrice(sellingPrice);
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setType(InventoryTransaction.TransactionType.OUTBOUND);
                transaction.setQuantityOnHand(p.getQuantityOnHand());
                transactionRepository.save(transaction);  // Save transaction

                return "redirect:/inventory";
            } else {
                model.addAttribute("error", "Not enough stock to remove.");
                return "inventory/error";
            }
        }
        model.addAttribute("error", "Product not found.");
        return "inventory/error";
    }


    private InventoryTransaction createInventoryTransaction(Product product, int quantity, InventoryTransaction.TransactionType type) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setType(type);
        transaction.setQuantityOnHand(calculateNewQuantityOnHand(product.getId(), quantity, type));
        return transaction;
    }

    private int calculateNewQuantityOnHand(Long productId, int quantity, InventoryTransaction.TransactionType type) {
        Optional<InventoryTransaction> latestTransaction = transactionRepository.findTopByProductIdOrderByTransactionDateDesc(productId);
        int currentQuantityOnHand = latestTransaction.map(InventoryTransaction::getQuantityOnHand).orElse(0);
        return type == InventoryTransaction.TransactionType.INBOUND
                ? currentQuantityOnHand + quantity
                : currentQuantityOnHand - quantity;
    }
}
