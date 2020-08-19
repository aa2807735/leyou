package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.vo.ExceptionResult;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.EnumMap;

/**
 * ClassName: CommonException <br/>
 * Description: TODO
 * Date 2020/4/28 9:11
 *
 * @author Lenovo
 **/
@ControllerAdvice
public class CommonException {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<ExceptionResult> handleException(MyException em) {
        ExceptionEnums enums = em.getExceptionEnums();
        Integer code = enums.getCode();
        String Message = enums.getMsg();
        return ResponseEntity.status(code).body(new ExceptionResult(enums));
    }
}
