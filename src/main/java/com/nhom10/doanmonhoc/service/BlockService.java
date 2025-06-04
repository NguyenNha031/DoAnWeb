package com.nhom10.doanmonhoc.service;

import com.nhom10.doanmonhoc.model.Block;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlockService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertBlockNative(Block block) {
        entityManager.createNativeQuery(
                        "INSERT INTO block (code,id_post,id_page) VALUES (?,?,?)")
                .setParameter(1, block.getCode())
                .setParameter(2, block.getIdPost())
                .setParameter(3, block.getIdPage())
                .executeUpdate();
    }
}
