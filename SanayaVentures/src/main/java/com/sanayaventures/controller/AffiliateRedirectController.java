
package com.sanayaventures.controller;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sanayaventures.entity.AffiliateClick;
import com.sanayaventures.entity.Product;
import com.sanayaventures.repository.AffiliateClickRepository;
import com.sanayaventures.repository.ProductRepository;

@Controller
@RequestMapping("/go")
public class AffiliateRedirectController {

    private final ProductRepository productRepository;
    private final AffiliateClickRepository affiliateClickRepository;

    public AffiliateRedirectController(
            ProductRepository productRepository,
            AffiliateClickRepository affiliateClickRepository) {

        this.productRepository = productRepository;
        this.affiliateClickRepository = affiliateClickRepository;
    }

    @GetMapping("/{slug}/{marketplace}")
    public String trackAndRedirect(
            @PathVariable String slug,
            @PathVariable String marketplace) {

        Optional<Product> result = productRepository.findBySlug(slug);

        if (result.isEmpty()) {
            return "redirect:/shop";
        }

        Product product = result.get();
        String store = marketplace.toLowerCase(Locale.ROOT);
        String destination;

        switch (store) {
            case "amazon":
                destination = product.getAmazonLink();
                break;

            case "flipkart":
                destination = product.getFlipkartLink();
                break;

            case "meesho":
                destination = product.getMeeshoLink();
                break;

            case "myntra":
                destination = product.getMyntraLink();
                break;

            default:
                return "redirect:/product/" + product.getSlug();
        }

        if (destination == null || destination.isBlank()) {
            return "redirect:/product/" + product.getSlug();
        }

        URI uri;

        try {
            uri = URI.create(destination.trim());
        } catch (IllegalArgumentException ex) {
            return "redirect:/product/" + product.getSlug();
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (scheme == null || host == null
                || !(scheme.equalsIgnoreCase("https")
                || scheme.equalsIgnoreCase("http"))) {
            return "redirect:/product/" + product.getSlug();
        }

        host = host.toLowerCase(Locale.ROOT);

        if (!isApprovedMarketplace(store, host)) {
            return "redirect:/product/" + product.getSlug();
        }

        // Record the click only after validating the destination.
        AffiliateClick click = new AffiliateClick(
                product.getSlug(),
                product.getTitle(),
                store);

        affiliateClickRepository.save(click);

        return "redirect:" + uri.toASCIIString();
    }

    private boolean isApprovedMarketplace(String store, String host) {

        switch (store) {
            case "amazon":
                return isAllowedHost(host, Set.of(
                        "amazon.in",
                        "www.amazon.in",
                        "amazon.com",
                        "www.amazon.com",
                        "amzn.to"));

            case "flipkart":
                return isAllowedHost(host, Set.of(
                        "flipkart.com",
                        "www.flipkart.com"));

            case "meesho":
                return isAllowedHost(host, Set.of(
                        "meesho.com",
                        "www.meesho.com"));

            case "myntra":
                return isAllowedHost(host, Set.of(
                        "myntra.com",
                        "www.myntra.com"));

            default:
                return false;
        }
    }

    private boolean isAllowedHost(String host, Set<String> allowedHosts) {
        return allowedHosts.contains(host);
    }
}
