package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.exception.ServiceException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for task api
 */
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
    public List<TaskDto> getTasks(int page, int limit) {
        List<TaskDto> returnVal = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        if(page > 0) page-=1;

        Pageable pageable =  PageRequest.of(page,limit);
        Page<TaskEntity> tasksPage = taskRepo.findAll(pageable);
        List<TaskEntity> tasks = tasksPage.getContent();

        for(TaskEntity taskEntity: tasks)
        {
            TaskDto taskDto = modelMapper.map(taskEntity, TaskDto.class);
            returnVal.add(taskDto);

        }
        return returnVal;
    }

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
        boolean hasNewUser = false;
        boolean hasNewTeam = false;
        UserDto newUser = null;
        TeamDto newTeam = null;

        if(task.getTitle() == null ) throw  new ServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        if(taskRepo.findByTitle((task.getTitle())) != null)
            throw  new ServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        task.setTaskId(utils.generateTaskId(20));

        TeamDto team = task.getTeamDetails();
        if (team != null) {
            TeamEntity teamFromRepo = teamRepo.findByTeamId(team.getTeamId());
            if (teamFromRepo != null) {
                newTeam = modelMapper.map(teamFromRepo, TeamDto.class);
            } else {
                hasNewTeam = true;
                newTeam = team;
                newTeam.setTeamId(utils.generateTeamId(20));
            }
            newTeam.getTasks().add(task);
            task.setTeamDetails(newTeam);
        }

        UserDto user = task.getUserDetails();
        if (user != null) {
            UserEntity userFromRepo = userRepo.findByUserId(user.getUserId());
            if (userFromRepo != null) {
                newUser = modelMapper.map(userFromRepo, UserDto.class);
            } else {
                hasNewUser = true;
                newUser = user;
                newUser.setUserId(utils.generateUserId(20));
            }
            newUser.getTasks().add(task);
            task.setUserDetails(newUser);
        }


        if(newTeam!= null && newUser != null && (hasNewTeam || hasNewUser)){
            newTeam.getUsers().add(newUser);
            newUser.getTeams().add(newTeam);
        }
        else if(!hasNewTeam && !hasNewUser){
            if(!checkIfUserInTeam(task))
                throw new ServiceException(ErrorMessages.USER_IS_NOT_IN_TEAM.getErrorMessage());
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
    public TaskDto updateTask(String taskId, TaskDto taskDto) {
        if(taskDto.getTitle() == null ) throw  new ServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);
        if (taskEntity == null)
            throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        if(!checkIfUserInTeam(taskDto))
            throw new ServiceException(ErrorMessages.USER_IS_NOT_IN_TEAM.getErrorMessage());


        ModelMapper modelMapper = new ModelMapper();

        taskEntity.setTitle(taskDto.getTitle());
        taskEntity.setDescription(taskDto.getDescription());
        taskEntity.setStatus(taskDto.getStatus());
        updateUsers(taskDto,taskEntity);
        updateTeams(taskDto,taskEntity);

        try {
            TaskEntity updatedTask = taskRepo.save(taskEntity);
            return modelMapper.map(updatedTask, TaskDto.class);
        }catch (Exception e){
            throw new ServiceException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());
        }
    }

    @Override
    public void deleteTask(String taskId) {
        TaskEntity taskEntity = taskRepo.findByTaskId(taskId);
        if (taskEntity == null)
            throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        try {
            taskRepo.delete(taskEntity);
        }catch (Exception e){
            throw new ServiceException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        }
    }


    /**
     * update users with new task details
     * @param updatedTask updated task from client
     * @param taskToUpdate task entity in Db
     */
    private void updateUsers(TaskDto updatedTask, TaskEntity taskToUpdate){
        UserEntity newUserFromRepo;

        if (updatedTask.getUserDetails() != null) {
            newUserFromRepo = userRepo.findByUserId(updatedTask.getUserDetails().getUserId());
            // task has an assigned user
            if (taskToUpdate.getUserDetails() != null) {
                // new user assigned to task
                if (!updatedTask.getUserDetails().getUserId().equals(taskToUpdate.getUserDetails().getUserId())) {
                    // remove task from old user
                    UserEntity oldUserFromRepo = userRepo.findByUserId(taskToUpdate.getUserDetails().getUserId());
                    oldUserFromRepo.removeTask(taskToUpdate);
                }
            }
            // add task to new user
            newUserFromRepo.addTask(taskToUpdate);
        } else {
            // task has an assigned user
            if (taskToUpdate.getUserDetails() != null) {
                UserEntity oldUserFromRepo = userRepo.findByUserId(taskToUpdate.getUserDetails().getUserId());
                oldUserFromRepo.removeTask(taskToUpdate);
            }
        }
    }

    /**
     * update team with new task details
     * @param updatedTask updated task from client
     * @param taskToUpdate task entity in Db
     */
    private void updateTeams(TaskDto updatedTask, TaskEntity taskToUpdate){
        TeamEntity newTeamFromRepo;

        if (updatedTask.getTeamDetails() != null) {
            newTeamFromRepo = teamRepo.findByTeamId(updatedTask.getTeamDetails().getTeamId());
            // task has an assigned team
            if (taskToUpdate.getTeamDetails() != null) {
                // new team assigned to task
                if (!updatedTask.getTeamDetails().getTeamId().equals(taskToUpdate.getTeamDetails().getTeamId())) {
                    // remove task from old team
                    TeamEntity oldTeamFromRepo = teamRepo.findByTeamId(taskToUpdate.getTeamDetails().getTeamId());
                    oldTeamFromRepo.removeTask(taskToUpdate);
                }
            }
            // add task to new team
            newTeamFromRepo.addTask(taskToUpdate);
        } else {
            // task has an assigned team
            if (taskToUpdate.getTeamDetails() != null) {
                TeamEntity oldTeamFromRepo = teamRepo.findByTeamId(taskToUpdate.getTeamDetails().getTeamId());
                oldTeamFromRepo.removeTask(taskToUpdate);
            }
        }
    }

    /**
     * check if user of current task is a member of the team of current task
     * @param task task to check
     * @return is user in team
     */
    private boolean checkIfUserInTeam(TaskDto task){
        if(task.getTeamDetails() != null && task.getUserDetails() != null){
            TeamEntity team = teamRepo.findByTeamId(task.getTeamDetails().getTeamId());
            UserEntity user  = userRepo.findByUserId(task.getUserDetails().getUserId());
            if(team != null){
                if(user != null){
                    return team.getUsers().contains(user);
                }
            }
        }
        return true;
    }
}
