package com.leyou.api.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.api.mapper.CategoryMapper;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * ClassName: CategoryService <br/>
 * Description: TODO
 * Date 2020/4/28 14:42
 *
 * @author Lenovo
 **/
@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据parentId进行查询其下面的子类
     * @param pid
     * @return
     */
    public List<Category> queryCategoryListByPid(Long pid) {


//        Example example = new Example(Category.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("parentId",pid);
        Category category = new Category();
        category.setParentId(pid);
        //会把对象中的非空属性作为查询对象
        List<Category> list = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(list)){
            throw new MyException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    /**
     * 根据一级、二级、三级目录id查询category对象
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids){
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)){
            throw new MyException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    @Transactional
    public void addCategoryNode(Category category) {
        Long parentId = category.getParentId();
        Category parent = categoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()){  //如果不是父亲
            parent.setIsParent(true);
            categoryMapper.updateByPrimaryKey(parent);  //更新
        }
        categoryMapper.insertSelective(category);
    }
}
