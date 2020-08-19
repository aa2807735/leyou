package com.leyou.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: annotation <br/>
 * Description: TODO
 * Date 2020/6/4 7:42
 * 标记注解
 * 运行时期运行
 * 方法上注解
 * @author Lenovo
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdminOnly {
}
