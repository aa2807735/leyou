package com.leyou.api.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT * FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id=cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);

    @Select("SELECT tb_brand.id, tb_brand.NAME,tb_brand.image,tb_brand.letter,tb_category.`name` AS cids FROM tb_brand JOIN tb_category_brand ON tb_brand.id = tb_category_brand.brand_id JOIN tb_category ON tb_category_brand.category_id = tb_category.id WHERE tb_brand.id = #{bid}")
    Brand queryByBrandId(@Param("bid") Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id=#{bid}")
    int DeleteCategoryBrand(@Param("bid")Long bid);
}

