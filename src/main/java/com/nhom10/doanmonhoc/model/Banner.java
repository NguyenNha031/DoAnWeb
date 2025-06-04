package com.nhom10.doanmonhoc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banner")
    private Long idBanner;

    @Column(name = "anh")
    private String image;

    private String mota;

    @Column(name = "id_site")
    private Long idSite;

}