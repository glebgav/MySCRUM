package com.sadna.app.ws.MySCRUM.ui.controller;

import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.Utils;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.UserDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;


    @GetMapping
    public String getUser()
    {
        return "sdfs";
    }


    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)
    {
        UserRest returnVal = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails,userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnVal);
        return returnVal;
    }

    @PutMapping
    public String updateUser()
    {
        return "sdfs";
    }

    @DeleteMapping
    public String deleteUser()
    {
        return "sdfs";
    }
}
