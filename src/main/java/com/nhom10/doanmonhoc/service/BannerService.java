package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Banner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BannerService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertNativeBanner(Banner banner) {
        Query query1 = entityManager.createNativeQuery("INSERT INTO banner (anh,mota,id_site) VALUES (?,?,?)")
                .setParameter(1,banner.getImage())
                .setParameter(2,banner.getMota())
                .setParameter(3,banner.getIdSite());
        query1.executeUpdate();
    }
    @Transactional
    public void deleteBanner(long id_site) {
        Query query = entityManager.createNativeQuery("DELETE FROM banner WHERE id_site=?")
                .setParameter(1,id_site);
        entityManager.joinTransaction();
        query.executeUpdate();
    }
}