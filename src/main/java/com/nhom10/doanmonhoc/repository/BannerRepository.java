package com.nhom10.doanmonhoc.repository;
import com.nhom10.doanmonhoc.model.Banner;
import com.nhom10.doanmonhoc.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIdSiteOrderByIdBannerAsc(Long idSite);
}