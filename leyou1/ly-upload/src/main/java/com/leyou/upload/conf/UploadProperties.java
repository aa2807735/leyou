package com.leyou.upload.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * ClassName: UploadProperties <br/>
 * Description: TODO
 * Date 2020/4/29 19:54
 *
 * @author Lenovo
 **/
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
