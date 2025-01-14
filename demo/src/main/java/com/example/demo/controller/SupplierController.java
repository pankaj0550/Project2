package com.example.demo.controller;

import com.example.demo.model.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public String getAllSuppliers(Model model) {
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "suppliers/list"; // Thymeleaf template: list.html
    }

    @GetMapping("/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/add"; // Thymeleaf template: add.html
    }

    @PostMapping("/add")
    public String addSupplier(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result) {
        if (result.hasErrors()) {
            return "suppliers/add";
        }
        supplierRepository.save(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String showEditSupplierForm(@PathVariable Long id, Model model) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "suppliers/edit";
        }
        return "redirect:/suppliers";
    }

    @PostMapping("/edit/{id}")
    public String updateSupplier(@PathVariable Long id, @Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result) {
        if (result.hasErrors()) {
            return "suppliers/edit";
        }
        supplier.setId(id);
        supplierRepository.save(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (productRepository.existsBySupplierId(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete supplier; associated products exist.");
            return "redirect:/suppliers";
        }
        supplierRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully.");
        return "redirect:/suppliers";
    }
}
