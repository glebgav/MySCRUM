package com.sadna.app.ws.MySCRUM.ui.controller;

import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.UserDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.OperationStatusModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.RequestOperationStatus;
import com.sadna.app.ws.MySCRUM.ui.model.response.TaskRest;
import com.sadna.app.ws.MySCRUM.ui.model.response.UserRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;


    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id)
    {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = userService.getUserByUserId(id);

        return modelMapper.map(userDto,UserRest.class);

    }


    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)
    {
        ModelMapper modelMapper = new ModelMapper();

        UserDto userDto = modelMapper.map(userDetails,UserDto.class);
        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser,UserRest.class);

    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
    {
        ModelMapper modelMapper = new ModelMapper();

        UserDto userDto = modelMapper.map(userDetails,UserDto.class);
        UserDto createdUser = userService.updateUser(id, userDto);

        return modelMapper.map(createdUser,UserRest.class);

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
        ModelMapper modelMapper = new ModelMapper();
        List<UserDto> usersDtoList = userService.getUsers(page, limit);

        for(UserDto userDto: usersDtoList)
        {
            UserRest userModel = modelMapper.map(userDto,UserRest.class);
            returnVal.add(userModel);

        }
        return  returnVal;
    }

    @GetMapping(path = "/{id}/tasks")
    public List<TaskRest> getUserTasks(@PathVariable String id)
    {
        ModelMapper modelMapper = new ModelMapper();
        List<TaskRest> returnVal = new ArrayList<>();

        List<TaskDto> taskDtoList = taskService.getTasksByUserId(id);

        if(taskDtoList != null && !taskDtoList.isEmpty()){
            Type listType = new TypeToken<List<TaskRest>>(){}.getType();
            returnVal = modelMapper.map(taskDtoList,listType);
        }

        return returnVal;

    }

    @GetMapping(path = "/{userId}/tasks/{taskId}")
    public TaskRest getUserTask(@PathVariable String userId, @PathVariable String taskId)
    {
        TaskRest returnVal = null;
        if(userService.getUserByUserId(userId) != null){
            TaskDto taskDto = taskService.getTask(taskId);
            if(taskDto != null){
                returnVal = new ModelMapper().map(taskDto,TaskRest.class);
            }
        }
        return returnVal;

    }
}
