
package com.sanayaventures.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sanayaventures.entity.Product;
import com.sanayaventures.repository.ProductRepository;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    private final ProductRepository productRepository;

    public AdminProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Admin dashboard
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        return "admin-dashboard";
    }

    // Display all products
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin-products";
    }

    // Show add product form
    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Add New Product");
        return "admin-product-form";
    }

    // Show edit product form
    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Product not found.");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Edit Product");

        return "admin-product-form";
    }

    // Save or update product
    @PostMapping("/products/save")
    public String saveProduct(
            Product product,
            RedirectAttributes redirectAttributes) {

        // Choose the correct form page if validation fails
        String formRedirect = product.getId() == null
                ? "redirect:/admin/products/new"
                : "redirect:/admin/products/edit/" + product.getId();

        // Validate product title
        if (product.getTitle() == null
                || product.getTitle().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Product title is required.");

            return formRedirect;
        }

        // Validate product price
        if (product.getPrice() == null
                || !Double.isFinite(product.getPrice())
                || product.getPrice() < 0) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Enter a valid product price.");

            return formRedirect;
        }

        // Verify product exists when editing
        if (product.getId() != null) {
            boolean exists = productRepository.existsById(product.getId());

            if (!exists) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage", "Product not found.");

                return "redirect:/admin/products";
            }
        }

        // Validate affiliate URLs
        String linkError;

        linkError = validateAffiliateLink(
                product.getAmazonLink(), "Amazon");
        if (linkError != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", linkError);
            return formRedirect;
        }

        linkError = validateAffiliateLink(
                product.getFlipkartLink(), "Flipkart");
        if (linkError != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", linkError);
            return formRedirect;
        }

        linkError = validateAffiliateLink(
                product.getMeeshoLink(), "Meesho");
        if (linkError != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", linkError);
            return formRedirect;
        }

        linkError = validateAffiliateLink(
                product.getMyntraLink(), "Myntra");
        if (linkError != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", linkError);
            return formRedirect;
        }

        // Generate a unique slug
        String baseSlug = makeSlug(product.getTitle());
        String slug = baseSlug;
        int suffix = 2;

        while (true) {
            Product sameSlug = productRepository.findBySlug(slug)
                    .orElse(null);

            if (sameSlug == null
                    || (product.getId() != null
                    && sameSlug.getId().equals(product.getId()))) {
                break;
            }

            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        product.setSlug(slug);

        // Save the product only after validation succeeds
        productRepository.save(product);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Product saved successfully.");

        return "redirect:/admin/products";
    }

    // Validate an optional affiliate URL

private String validateAffiliateLink(String link, String platform) {

    // Blank affiliate links are allowed
    if (link == null || link.isBlank()) {
        return null;
    }

    try {
        URI uri = new URI(link.trim());

        String scheme = uri.getScheme();
        String host = uri.getHost();

        // HTTPS is required
        if (scheme == null
                || !scheme.equalsIgnoreCase("https")) {
            return platform + " link must use HTTPS.";
        }

        // Hostname is required
        if (host == null || host.isBlank()) {
            return platform + " link must be a valid URL.";
        }

        host = host.toLowerCase(Locale.ROOT);

        // Check the marketplace domain
        boolean validHost;

        switch (platform.toLowerCase(Locale.ROOT)) {
            case "amazon":
                validHost = host.equals("amazon.in")
                        || host.endsWith(".amazon.in");
                break;

            case "flipkart":
                validHost = host.equals("flipkart.com")
                        || host.endsWith(".flipkart.com");
                break;

            case "meesho":
                validHost = host.equals("meesho.com")
                        || host.endsWith(".meesho.com");
                break;

            case "myntra":
                validHost = host.equals("myntra.com")
                        || host.endsWith(".myntra.com");
                break;

            default:
                return "Unknown affiliate marketplace.";
        }

        if (!validHost) {
            return platform
                    + " link must belong to the correct marketplace.";
        }

        return null;

    } catch (URISyntaxException e) {
        return platform + " link is not a valid URL.";
    }
}


    // Delete product
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Product deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Product not found.");
        }

        return "redirect:/admin/products";
    }

    // Generate URL-friendly product slug
    private String makeSlug(String title) {

        String normalized = Normalizer.normalize(
                title.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD);

        String slug = Pattern.compile("\\p{M}+")
                .matcher(normalized)
                .replaceAll("");

        slug = slug.replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-|-$", "");

        if (slug.isBlank()) {
            return "product";
        }

        return slug;
    }
}
