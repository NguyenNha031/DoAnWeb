package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByIdSiteOrderByIdMenuAsc(Long idSite);
}