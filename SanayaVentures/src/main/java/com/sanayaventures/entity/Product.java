
package com.sanayaventures.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String category;

    private Double price;
    private Double mrp;

    @Column(length = 1000)
    private String shortDescription;

    @Column(length = 5000)
    private String longDescription;

    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String image5;

    private String brand;

    @Column(length = 3000)
    private String specifications;

    private String amazonLink;
    private String flipkartLink;
    private String meeshoLink;
    private String myntraLink;

    public Product() {
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() { return longDescription; }
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getImage1() { return image1; }
    public void setImage1(String image1) { this.image1 = image1; }

    public String getImage2() { return image2; }
    public void setImage2(String image2) { this.image2 = image2; }

    public String getImage3() { return image3; }
    public void setImage3(String image3) { this.image3 = image3; }

    public String getImage4() { return image4; }
    public void setImage4(String image4) { this.image4 = image4; }

    public String getImage5() { return image5; }
    public void setImage5(String image5) { this.image5 = image5; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getAmazonLink() { return amazonLink; }
    public void setAmazonLink(String amazonLink) {
        this.amazonLink = amazonLink;
    }

    public String getFlipkartLink() { return flipkartLink; }
    public void setFlipkartLink(String flipkartLink) {
        this.flipkartLink = flipkartLink;
    }

    public String getMeeshoLink() { return meeshoLink; }
    public void setMeeshoLink(String meeshoLink) {
        this.meeshoLink = meeshoLink;
    }

    public String getMyntraLink() { return myntraLink; }
    public void setMyntraLink(String myntraLink) {
        this.myntraLink = myntraLink;
    }
}
