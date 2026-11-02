
package com.sanayaventures.controller;

import com.sanayaventures.entity.Product;
import com.sanayaventures.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.Set;

@RestController
public class SitemapController {

    private final ProductRepository productRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public SitemapController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    public String sitemap() {

        String base = baseUrl.replaceAll("/+$", "");

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        addUrl(xml, base + "/");
        addUrl(xml, base + "/shop");
        addUrl(xml, base + "/about-us");
        addUrl(xml, base + "/contact-us");
        addUrl(xml, base + "/privacy-policy");
        addUrl(xml, base + "/faq");

        Set<String> categories = new LinkedHashSet<>();

        for (Product product : productRepository.findAll()) {
            if (product.getSlug() != null && !product.getSlug().isBlank()) {
                addUrl(xml, base + "/product/" + product.getSlug());
            }

            if (product.getCategory() != null
                    && !product.getCategory().isBlank()) {
                categories.add(product.getCategory());
            }
        }

        for (String category : categories) {
            addUrl(xml, base + "/category/" + slugify(category));
        }

        xml.append("</urlset>");

        return xml.toString();
    }

    private void addUrl(StringBuilder xml, String url) {
        xml.append("<url><loc>")
           .append(escapeXml(url))
           .append("</loc></url>");
    }

    private String slugify(String value) {
        return value.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private String escapeXml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
