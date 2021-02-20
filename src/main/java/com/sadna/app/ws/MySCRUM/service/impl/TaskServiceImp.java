package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.TaskRepository;
import com.sadna.app.ws.MySCRUM.io.repository.TeamRepository;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    TeamRepository teamRepo;


    @Autowired
    TaskRepository taskRepo;

    @Override
    public List<TaskDto> getTasksByUserId(String id) {
        List<TaskDto> returnVal = new ArrayList<>();
        UserEntity userEntity = userRepo.findByUserId(id);
        if(userEntity==null)
           return returnVal;

        ModelMapper modelMapper = new ModelMapper();
        Iterable<TaskEntity> taskEntityList = taskRepo.findAllByUserDetails(userEntity);
        if(taskEntityList==null) return returnVal;

        for(TaskEntity task: taskEntityList){
            returnVal.add(modelMapper.map(task,TaskDto.class));
        }

        return returnVal;
    }

    @Override
    public TaskDto getTask(String taskId) {
        TaskDto returnVal = null;

        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);

        if(taskEntity != null){
            returnVal = new ModelMapper().map(taskEntity,TaskDto.class);
        }

        return returnVal;

    }

    @Override
    public List<TaskDto> getTasksByTeamId(String teamId) {
        List<TaskDto> returnVal = new ArrayList<>();
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if(teamEntity==null)
            return returnVal;

        ModelMapper modelMapper = new ModelMapper();
        Iterable<TaskEntity> taskEntityList = taskRepo.findAllByTeamDetails(teamEntity);
        if(taskEntityList==null) return returnVal;

        for(TaskEntity task: taskEntityList){
            returnVal.add(modelMapper.map(task,TaskDto.class));
        }

        return returnVal;
    }
}
