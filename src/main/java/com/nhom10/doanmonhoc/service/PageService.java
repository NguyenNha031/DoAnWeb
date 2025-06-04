package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Page;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class PageService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertPageNative(Page page) {
        entityManager.createNativeQuery(
                        "INSERT INTO page (title,status,mota,id_menu) VALUES (?,?,?,?)")
                .setParameter(1, page.getTitle())
                .setParameter(2, page.getStatus().toString())
                .setParameter(3, page.getMota())
                .setParameter(4, page.getIdMenu())
                .executeUpdate();
    }
}
