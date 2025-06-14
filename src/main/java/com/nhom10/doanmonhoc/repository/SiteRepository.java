package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findAllByOrderByIdSiteAsc();
    Site findFirstByOrderByIdSiteAsc();
}