package com.leyou.gatway.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * ClassName: FilterProperties <br/>
 * Description: TODO
 * Date 2020/5/9 8:19
 *
 * @author Lenovo
 **/
@Data
@ConfigurationProperties(prefix = "leyou.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
