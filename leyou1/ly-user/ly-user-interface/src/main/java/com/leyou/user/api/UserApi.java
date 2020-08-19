package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: UserApi <br/>
 * Description: TODO
 * Date 2020/5/8 13:27
 *
 * @author Lenovo
 **/
public interface UserApi {
    @GetMapping("/query")
    User queryUserByUsernameAndPassword(@RequestParam("username") String username,
                                        @RequestParam("password") String password);
}
