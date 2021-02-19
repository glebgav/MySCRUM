package com.sadna.app.ws.MySCRUM.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.Utils;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.UserDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.OperationStatusModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.RequestOperationStatus;
import com.sadna.app.ws.MySCRUM.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;


    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id)
    {
        UserRest returnVal = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto,returnVal);

        return returnVal;
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

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
    {
        UserRest returnVal = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails,userDto);

        UserDto createdUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(createdUser,returnVal);
        return returnVal;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnVal = new OperationStatusModel();

        userService.deleteUser(id);
        returnVal.setOperationName("Delete");
        returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnVal;

    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page
    ,@RequestParam(value = "limit", defaultValue = "50") int limit){
        List<UserRest> returnVal = new ArrayList<>();

        List<UserDto> usersDtoList = userService.getUsers(page, limit);

        for(UserDto userDto: usersDtoList)
        {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto,userModel);
            returnVal.add(userModel);

        }
        return  returnVal;
    }
}
