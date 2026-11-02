
package com.sanayaventures.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanayaventures.entity.Product;
import com.sanayaventures.repository.ProductRepository;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Homepage
    @GetMapping("/")
    public String home(Model model) {

        List<Product> products = productRepository.findAll();

        model.addAttribute("products",
                products.stream().limit(8).toList());

        return "index";
    }

    // Shop page
    @GetMapping("/shop")
    public String shop(Model model) {

        model.addAttribute("products",
                productRepository.findAll());

        return "shop";
    }


    // Category page
    @GetMapping("/category/{categoryName}")
    public String category(@PathVariable String categoryName, Model model) {

        String category = categoryName.replace("-", " ");

        // Match the homepage category URL to the database category
        if (category.equalsIgnoreCase("electrical and electronics")) {
            category = "Electronics";
        }

        model.addAttribute("categoryName", category);

        model.addAttribute(
                "products",
                productRepository.findByCategoryIgnoreCase(category)
        );

        return "category";
    }


    // Product detail page
    @GetMapping("/product/{productSlug}")
    public String product(
            @PathVariable String productSlug,
            Model model) {

        Product product = productRepository
                .findBySlug(productSlug)
                .orElse(null);

        if (product == null) {
            return "redirect:/shop";
        }

        model.addAttribute("product", product);

        return "product";
    }

    // Search by product title, category, or brand

    // Live product search suggestions
    @GetMapping("/search/suggestions")
    @ResponseBody
    public List<Map<String, Object>> searchSuggestions(
            @RequestParam(defaultValue = "") String q) {

        String query = q == null ? "" : q.trim();

        // Avoid showing every product for an empty or short query
        if (query.length() < 2) {
            return List.of();
        }

        return productRepository
                .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(
                        query, query, query)
                .stream()
                .limit(6)
                .map(product -> {
                    Map<String, Object> item = new HashMap<>();

                    item.put("title", product.getTitle());
                    item.put("slug", product.getSlug());
                    item.put("category", product.getCategory());
                    item.put("price", product.getPrice());
                    item.put("image1", product.getImage1());

                    return item;
                })
                .toList();
    }

    // About Us
    @GetMapping("/about-us")
    public String about() {
        return "about";
    }
    

    // Full search results page
    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "") String q,
            Model model) {

        String query = q == null ? "" : q.trim();

        List<Product> products;

        if (query.isEmpty()) {
            products = List.of();
        } else {
            products = productRepository
                    .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(
                            query, query, query);
        }

        model.addAttribute("products", products);
        model.addAttribute("query", query);

        return "search";
    }


    // Contact Us
    @GetMapping("/contact-us")
    public String contact() {
        return "contact";
    }

    // Privacy Policy
    @GetMapping("/privacy-policy")
    public String privacy() {
        return "privacy";
    }
    
 // FAQ Page
    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }
}
