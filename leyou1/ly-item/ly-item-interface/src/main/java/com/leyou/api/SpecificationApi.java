package com.leyou.api;

import com.leyou.pojo.SpecParam;
import com.leyou.pojo.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * ClassName: SpectificationApi <br/>
 * Description: TODO
 * Date 2020/5/3 10:20
 *
 * @author Lenovo
 **/
public interface SpecificationApi {
    @GetMapping("spec/groups/{cid}")
    List<Specification> queryGroupByCid(@PathVariable("cid") Long cid);

    /**
     * 查询参数集合
     *
     * @param gid
     * @param cid
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamList(@RequestParam(value = "gid", required = false) Long gid,
                                   @RequestParam(value = "cid", required = false) Long cid,
                                   @RequestParam(value = "searching", required = false) Boolean searching);

    @GetMapping("spec/group")
    List<Specification> queryGroupItemByCid(@RequestParam("cid") Long cid);
}
