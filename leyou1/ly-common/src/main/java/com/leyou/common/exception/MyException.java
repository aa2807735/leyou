package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ClassName: MyException <br/>
 * Description: TODO
 * Date 2020/4/28 9:00
 *
 * @author Lenovo
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MyException extends RuntimeException{
    private ExceptionEnums exceptionEnums;

}
