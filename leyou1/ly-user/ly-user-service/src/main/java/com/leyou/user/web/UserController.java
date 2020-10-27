package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * ClassName: UserController <br/>
 * Description: TODO
 * Date 2020/5/7 20:14
 *
 * @author Lenovo
 **/
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable(value = "data", required = true) String data,
                                             @PathVariable(value = "type", required = true) Integer type) {
        return ResponseEntity.ok(userService.checkData(data, type));
    }

    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone) {
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code) {
        if (result.hasErrors()) {
            throw new RuntimeException(result.getFieldErrors().stream().
                    map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(",")));
        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/query")
    public ResponseEntity<User> queryUserByUsernameAndPassword(@RequestParam("username") String username,
                                                               @RequestParam("password") String password){
        return ResponseEntity.ok(userService.queryUserByUsernameAndPassword(username,password));
    }

}
