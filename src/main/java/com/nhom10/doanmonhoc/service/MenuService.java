package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class MenuService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertMenuNative(Menu menu) {
        entityManager.createNativeQuery(
                        "INSERT INTO menu (name,id_site) VALUES (?,?)")
                .setParameter(1, menu.getName())
                .setParameter(2, menu.getIdSite())
                .executeUpdate();
    }
}
