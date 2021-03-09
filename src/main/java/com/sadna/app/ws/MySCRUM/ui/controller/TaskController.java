package com.sadna.app.ws.MySCRUM.ui.controller;

import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.service.TeamService;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.TaskDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.TaskRest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @PutMapping(path = "/{id}")
    public TaskRest updateTask(@PathVariable String id, @RequestBody TaskDetailsRequestModel taskDetails)
    {
        ModelMapper modelMapper = new ModelMapper();

        TaskDto taskDto = modelMapper.map(taskDetails,TaskDto.class);
        TaskDto updatedTask = taskService.updateTask(id, taskDto);

        return modelMapper.map(updatedTask,TaskRest.class);

    }
}
