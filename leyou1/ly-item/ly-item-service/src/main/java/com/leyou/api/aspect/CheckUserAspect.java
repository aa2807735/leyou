package com.leyou.api.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * ClassName: CheckUserAspect <br/>
 * Description: TODO
 * Date 2020/6/4 7:38
 *
 * @author Lenovo
 **/
@Aspect
@Component
public class CheckUserAspect {


    @Pointcut("@annotation(com.leyou.api.annotation.AdminOnly)")
    public void checkAdmin(){

    }


    @Before("checkAdmin()")
    public void check(){
        System.out.println("前置通知");
    }


}
