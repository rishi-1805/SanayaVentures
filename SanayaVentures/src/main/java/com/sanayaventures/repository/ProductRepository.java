
package com.sanayaventures.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanayaventures.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByTitleContainingIgnoreCase(String title);

    // Search by product title, category, or brand
    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(
            String title,
            String category,
            String brand
    );
    
    
}
