package com.sadna.app.ws.MySCRUM.ui.controller;

import com.sadna.app.ws.MySCRUM.exception.ServiceException;
import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.service.TeamService;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.UserDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents all the api for user management (REST controller)
 */
@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    TeamService teamService;

    ModelMapper modelMapper = new ModelMapper();



    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserDto userDto = userService.getUserByUserId(id);

        return modelMapper.map(userDto,UserRest.class);
    }


    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails,UserDto.class);
        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser,UserRest.class);

    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails,UserDto.class);
        UserDto createdUser = userService.updateUser(id, userDto);

        return modelMapper.map(createdUser,UserRest.class);

    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) throws ServiceException {

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
            UserRest userModel = modelMapper.map(userDto,UserRest.class);
            returnVal.add(userModel);

        }
        return  returnVal;
    }

    @GetMapping(path = "/{id}/tasks")
    public List<TaskRest> getUserTasks(@PathVariable String id)
    {
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

    @GetMapping(path = "/{id}/teams")
    public List<TeamRest> getUserTeams(@PathVariable String id)
    {
        List<TeamRest> returnVal = new ArrayList<>();

        List<TeamDto> teamDtoList = teamService.getTeamsByUserId(id);

        if(teamDtoList != null && !teamDtoList.isEmpty()){
            Type listType = new TypeToken<List<TeamRest>>(){}.getType();
            returnVal = modelMapper.map(teamDtoList,listType);
        }

        return returnVal;

    }

    @GetMapping(path = "/{id}/teams/tasks")
    public List<TaskRest> getUserTeamsTasks(@PathVariable String id)
    {
        List<TaskRest> returnVal = new ArrayList<>();

        List<TeamDto> teamDtoList = teamService.getTeamsByUserId(id);


        if(teamDtoList != null && !teamDtoList.isEmpty()){
            Type listType = new TypeToken<List<TaskRest>>(){}.getType();
            for(TeamDto team: teamDtoList){
                returnVal.addAll(modelMapper.map(team.getTasks(),listType));
            }
        }
        return returnVal;
    }
}
