package com.sanayaventures.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "affiliate_clicks")
public class AffiliateClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String productSlug;

    @Column(length = 255)
    private String productTitle;

    @Column(nullable = false, length = 30)
    private String marketplace;

    @Column(nullable = false)
    private LocalDateTime clickedAt;

    public AffiliateClick() {
    }

    public AffiliateClick(
            String productSlug,
            String productTitle,
            String marketplace) {
        this.productSlug = productSlug;
        this.productTitle = productTitle;
        this.marketplace = marketplace;
    }

    @PrePersist
    public void setClickTime() {
        if (clickedAt == null) {
            clickedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }
}