package com.leyou.api.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.api.mapper.SpecGroupMapper;
import com.leyou.api.mapper.SpecParamMapper;
import com.leyou.pojo.SpecParam;
import com.leyou.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: SpecificationService <br/>
 * Description: TODO
 * Date 2020/4/30 12:06
 *
 * @author Lenovo
 **/
@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<Specification> queryGroupByCid(Long cid) {
        Specification g = new Specification();
        g.setCid(cid);
        List<Specification> select = specGroupMapper.select(g);
        if (CollectionUtils.isEmpty(select)) {
            throw new MyException(ExceptionEnums.SPEC_GROUP_NOT_FOUND);
        }
        return select;
    }

    public List<SpecParam> queryParamByGid(Long gid, Long cid, Boolean searching) {
        SpecParam s = new SpecParam();
        s.setGroupId(gid);
        s.setCid(cid);
        s.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(s);
        if (CollectionUtils.isEmpty(list)) {
            throw new MyException(ExceptionEnums.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public List<Specification> queryGroupListByCid(Long cid) {
        List<Specification> specifications = this.queryGroupByCid(cid);
        List<SpecParam> specParams = queryParamByGid(null, cid, null);
        //先把规格参数编程map,key是规格组的id,map的值是组下的所有参数
        Map<Long, List<SpecParam>> params = new HashMap<>();
        for (SpecParam specParam : specParams) {
            if(!params.containsKey(specParam.getGroupId())){ //该组不存在
                params.put(specParam.getGroupId(),new ArrayList<>());
            }
            List<SpecParam> specParamsList = params.get(specParam.getGroupId());
            specParamsList.add(specParam);
            params.put(specParam.getGroupId(),specParamsList);
        }

        for (Specification specification : specifications) {
            specification.setParams(params.get(specification.getId()));
        }
        return specifications;
    }
}
