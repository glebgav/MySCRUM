package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.TaskRepository;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.response.ErrorMessages;
import com.sadna.app.ws.MySCRUM.ui.model.response.TaskRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    UserRepository userRepo;


    @Autowired
    TaskRepository taskRepository;

    @Override
    public List<TaskDto> getTasksByUserId(String id) {
        List<TaskDto> returnVal = new ArrayList<>();
        UserEntity userEntity = userRepo.findByUserId(id);
        if(userEntity==null)
           return returnVal;

        ModelMapper modelMapper = new ModelMapper();
        Iterable<TaskEntity> taskEntityList = taskRepository.findAllByUserDetails(userEntity);
        if(taskEntityList==null) return returnVal;

        for(TaskEntity task: taskEntityList){
            returnVal.add(modelMapper.map(task,TaskDto.class));
        }

        return returnVal;
    }

    @Override
    public TaskDto getTask(String taskId) {
        TaskDto returnVal = null;

        TaskEntity taskEntity = taskRepository.findByTaskId(taskId);

        if(taskEntity != null){
            returnVal = new ModelMapper().map(taskEntity,TaskDto.class);
        }

        return returnVal;

    }
}
