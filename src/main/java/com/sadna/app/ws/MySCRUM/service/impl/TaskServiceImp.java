package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.TaskRepository;
import com.sadna.app.ws.MySCRUM.io.repository.TeamRepository;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.shared.Utils;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    Utils utils;

    @Override
    public List<TaskDto> getTasksByUserId(String id) {
        List<TaskDto> returnVal = new ArrayList<>();
        UserEntity userEntity = userRepo.findByUserId(id);
        if (userEntity == null)
            return returnVal;

        ModelMapper modelMapper = new ModelMapper();
        Iterable<TaskEntity> taskEntityList = taskRepo.findAllByUserDetails(userEntity);
        if (taskEntityList == null) return returnVal;

        for (TaskEntity task : taskEntityList) {
            returnVal.add(modelMapper.map(task, TaskDto.class));
        }

        return returnVal;
    }

    @Override
    public TaskDto getTask(String taskId) {
        TaskDto returnVal = null;

        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);

        if (taskEntity != null) {
            returnVal = new ModelMapper().map(taskEntity, TaskDto.class);
        }

        return returnVal;

    }

    @Override
    public List<TaskDto> getTasksByTeamId(String teamId) {
        List<TaskDto> returnVal = new ArrayList<>();
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            return returnVal;

        ModelMapper modelMapper = new ModelMapper();
        Iterable<TaskEntity> taskEntityList = taskRepo.findAllByTeamDetails(teamEntity);
        if (taskEntityList == null) return returnVal;

        for (TaskEntity task : taskEntityList) {
            returnVal.add(modelMapper.map(task, TaskDto.class));
        }

        return returnVal;
    }

    @Override
    @Transactional
    public TaskDto createTask(TaskDto task) {
        ModelMapper modelMapper = new ModelMapper();
        task.setTaskId(utils.generateTaskId(20));

        TeamDto team = task.getTeamDetails();
        if (team != null) {
            TeamEntity teamFromRepo = teamRepo.findByTeamId(team.getTeamId());
            TeamDto newTeam;
            if (teamFromRepo != null) {
                newTeam = modelMapper.map(teamFromRepo, TeamDto.class);
            } else {
                newTeam = team;
                newTeam.setTeamId(utils.generateTaskId(20));
            }
            newTeam.getTasks().add(task);
            task.setTeamDetails(newTeam);
        }

        UserDto user = task.getUserDetails();
        if (user != null) {
            UserEntity userFromRepo = userRepo.findByUserId(user.getUserId());
            UserDto newUser;
            if (userFromRepo != null) {
                newUser = modelMapper.map(userFromRepo, UserDto.class);
            } else {
                newUser = user;
                newUser.setUserId(utils.generateTaskId(20));
            }
            newUser.getTasks().add(task);
            task.setUserDetails(newUser);
        }

        TaskEntity taskEntity = modelMapper.map(task, TaskEntity.class);


        if (taskEntity.getUserDetails() != null) {
            userRepo.save(taskEntity.getUserDetails());
        }

        if (taskEntity.getTeamDetails() != null) {
            teamRepo.save(taskEntity.getTeamDetails());
        }

        TaskEntity storedTaskDetails = taskRepo.save(taskEntity);


        return modelMapper.map(storedTaskDetails, TaskDto.class);

    }

    @Override
    @Transactional
    public TaskDto updateTask(String taskId, TaskDto taskDto) {
        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);
        if (taskEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();

        taskEntity.setTaskId(taskDto.getTaskId());
        taskEntity.setTitle(taskDto.getTitle());
        taskEntity.setDescription(taskDto.getDescription());
        taskEntity.setStatus(taskDto.getStatus());
        taskEntity.setUserDetails(updateUsers(taskDto,taskEntity));
        taskEntity.setTeamDetails(updateTeams(taskDto,taskEntity));

        TaskEntity updatedTask = taskRepo.save(taskEntity);

        return modelMapper.map(updatedTask, TaskDto.class);
    }

    @Override
    public void deleteTask(String taskId) {
        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);
        if (taskEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        taskRepo.delete(taskEntity);
    }

    private UserEntity updateUsers(TaskDto updatedTask, TaskEntity taskToUpdate){
        UserEntity newUserFromRepo = null;

        if (updatedTask.getUserDetails() != null) {
            newUserFromRepo = userRepo.findByUserId(updatedTask.getUserDetails().getUserId());
            // task has an assigned user
            if (taskToUpdate.getUserDetails() != null) {
                // new user assigned to task
                if (!updatedTask.getUserDetails().getUserId().equals(taskToUpdate.getUserDetails().getUserId())) {
                    // remove task from old user
                    UserEntity oldUserFromRepo = userRepo.findByUserId(taskToUpdate.getUserDetails().getUserId());
                    List<TaskEntity> oldTaskList = oldUserFromRepo.getTasks();
                    oldTaskList.remove(taskToUpdate);

                    // save changes to db
                    userRepo.save(oldUserFromRepo);
                }
            }
            // add task to new user
            List<TaskEntity> tasks = newUserFromRepo.getTasks();
            tasks.add(taskToUpdate);

            userRepo.save(newUserFromRepo);
        } else {
            // task has an assigned user
            if (taskToUpdate.getUserDetails() != null) {
                UserEntity oldUserFromRepo = userRepo.findByUserId(taskToUpdate.getUserDetails().getUserId());
                List<TaskEntity> oldTaskList = oldUserFromRepo.getTasks();
                oldTaskList.remove(taskToUpdate);

                userRepo.save(oldUserFromRepo);
            }
        }
        return newUserFromRepo;
    }

    private TeamEntity updateTeams(TaskDto updatedTask, TaskEntity taskToUpdate){
        TeamEntity newTeamFromRepo = null;

        if (updatedTask.getTeamDetails() != null) {
            newTeamFromRepo = teamRepo.findByTeamId(updatedTask.getTeamDetails().getTeamId());
            // task has an assigned team
            if (taskToUpdate.getTeamDetails() != null) {
                // new team assigned to task
                if (!updatedTask.getTeamDetails().getTeamId().equals(taskToUpdate.getTeamDetails().getTeamId())) {
                    // remove task from old team
                    TeamEntity oldTeamFromRepo = teamRepo.findByTeamId(taskToUpdate.getTeamDetails().getTeamId());
                    List<TaskEntity> oldTaskList = oldTeamFromRepo.getTasks();
                    oldTaskList.remove(taskToUpdate);

                    // save changes to db
                    teamRepo.save(oldTeamFromRepo);
                }
            }
            // add task to new team
            List<TaskEntity> tasks = newTeamFromRepo.getTasks();
            tasks.add(taskToUpdate);

            teamRepo.save(newTeamFromRepo);
        } else {
            // task has an assigned team
            if (taskToUpdate.getUserDetails() != null) {
                TeamEntity oldTeamFromRepo = teamRepo.findByTeamId(taskToUpdate.getTeamDetails().getTeamId());
                List<TaskEntity> oldTaskList = oldTeamFromRepo.getTasks();
                oldTaskList.remove(taskToUpdate);

                teamRepo.save(oldTeamFromRepo);
            }
        }
        return newTeamFromRepo;
    }
}
