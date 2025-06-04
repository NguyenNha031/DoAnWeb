package com.nhom10.doanmonhoc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "block")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_block")
    private Long idBlock;

    @Column(name = "code")
    private String code;

    @Column(name = "id_post")
    private Long idPost;

    @Column(name = "id_page")
    private Long idPage;


}