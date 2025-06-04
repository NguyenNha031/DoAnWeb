package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByIdPost(Long idPost);
    List<Block> findByIdPage(Long idPage);

}