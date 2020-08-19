package com.leyou.api.mapper;

import com.leyou.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * ClassName: CategoryMapper <br/>
 * Description: TODO
 * Date 2020/4/28 14:41
 *
 * @author Lenovo
 **/
public interface CategoryMapper  extends Mapper<Category>, IdListMapper<Category,Long> {
}
