package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.enums.Status;
import com.nhom10.doanmonhoc.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.idSite = :id AND p.status = :status ORDER BY p.pined DESC,p.createdAt DESC")
    List<Post> findPublishedPostsByIdSiteOrderByPined(Long id,@Param("status") Status status);
    Post findTopByOrderByIdPostDesc();
}