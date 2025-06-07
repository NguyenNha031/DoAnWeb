package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertPostNative(Post post) {
        entityManager.createNativeQuery(
                        """
                        INSERT INTO post (title, status, created_by, mota, pined, id_site)
                        VALUES (?1, CAST(?2 AS status), ?3, ?4, ?5, ?6)
                        """)
                .setParameter(1, post.getTitle())
                .setParameter(2, post.getStatus().name()) // .name() đảm bảo đúng Enum string
                .setParameter(3, post.getCreatedBy())
                .setParameter(4, post.getMota())
                .setParameter(5, post.getPined())
                .setParameter(6, post.getIdSite())
                .executeUpdate();
    }
}
