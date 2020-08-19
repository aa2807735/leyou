package com.leyou.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table(name = "tb_brand")
@Data
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;// 品牌名称
    private String image;// 品牌图片
    private Character letter;

    @Transient
    private String cids;
}