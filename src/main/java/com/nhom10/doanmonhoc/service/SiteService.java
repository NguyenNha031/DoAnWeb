package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Site;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertSiteNative(Site site) {
        entityManager.createNativeQuery(
                        "INSERT INTO site (site_name,logo) VALUES (?,?)")
                .setParameter(1, site.getName())
                .setParameter(2, site.getLogo())
                .executeUpdate();
    }
    @Transactional
    public void editSite(Site site) {
        Query query = entityManager.createNativeQuery("UPDATE site SET logo=? WHERE id_site=?")
                .setParameter(1,site.getLogo())
                .setParameter(2,site.getIdSite());
        entityManager.joinTransaction();
        query.executeUpdate();
    }
}