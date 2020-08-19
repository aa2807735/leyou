package com.leyou.api.web;

import com.leyou.api.service.SpecificationService;
import com.leyou.pojo.SpecParam;
import com.leyou.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: SpecificationController <br/>
 * Description: TODO
 * Date 2020/4/30 12:06
 *
 * @author Lenovo
 **/
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;


    @GetMapping("groups/{cid}")
    private ResponseEntity<List<Specification>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid
     * @param cid
     * @return
     */
    @GetMapping("params")
    private ResponseEntity<List<SpecParam>> queryParamList(@RequestParam(value = "gid",required = false)Long gid,
                                                            @RequestParam(value = "cid",required = false)Long cid,
                                                            @RequestParam(value = "searching",required = false)Boolean searching){

        return ResponseEntity.ok(specificationService.queryParamByGid(gid,cid,searching));
    }

    /**
     * 根据分类查询规格组
     * @param cid
     * @return
     */
    @GetMapping("group")
    public ResponseEntity<List<Specification>> queryGroupItemByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryGroupListByCid(cid));
    }


}
