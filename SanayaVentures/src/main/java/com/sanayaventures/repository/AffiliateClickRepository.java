package com.sanayaventures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sanayaventures.entity.AffiliateClick;

public interface AffiliateClickRepository
        extends JpaRepository<AffiliateClick, Long> {
}