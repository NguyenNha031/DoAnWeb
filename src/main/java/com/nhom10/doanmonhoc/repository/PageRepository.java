package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.enums.Status;
import com.nhom10.doanmonhoc.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    @Query("SELECT pa FROM Page pa WHERE pa.idMenu = :id AND pa.status = :status ORDER BY pa.createdAt ASC")
    List<Page> findPublishedPagesByIdMenuOrderByCreatedAt(Long id,@Param("status") Status status);
}