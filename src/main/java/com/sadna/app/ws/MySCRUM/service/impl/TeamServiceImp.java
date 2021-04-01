package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.exception.ServiceException;
import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.TaskRepository;
import com.sadna.app.ws.MySCRUM.io.repository.TeamRepository;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.TeamService;
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

@Service
public class TeamServiceImp implements TeamService {

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    Utils utils;

    @Override
    @Transactional
    public TeamDto createTeam(TeamDto team) {
        ModelMapper modelMapper = new ModelMapper();

        if(team.getName() == null) throw  new ServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        if(teamRepo.findByName((team.getName())) != null)
            throw  new ServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());


        if (team.getTasks() != null) {
            for (TaskDto task : team.getTasks()) {
                TaskEntity taskFromRepo = taskRepo.findByTaskId(task.getTaskId());
                TaskDto newTask;
                if (taskFromRepo != null) {
                    // check if for this task, the user is assigned to the team
                    if(!checkIfTaskIsValid(taskFromRepo, team.getUsers()))
                        throw new ServiceException(ErrorMessages.TASK_IS_ASSIGNED_TO_WRONG_USER.getErrorMessage());
                    newTask = modelMapper.map(taskFromRepo, TaskDto.class);
                } else {
                    newTask = task;
                    newTask.setTaskId(utils.generateUserId(20));
                }
                newTask.setTeamDetails(team);
            }
        }

        List<UserDto> usersList = team.getUsers();
        if (usersList != null) {
            for (int i = 0; i < usersList.size(); i++) {
                UserEntity userFromRepo = userRepo.findByUserId(usersList.get(i).getUserId());
                UserDto newUser;
                if (userFromRepo != null) {
                    newUser = modelMapper.map(userFromRepo, UserDto.class);
                } else {
                    newUser = usersList.get(i);
                    newUser.setUserId(utils.generateUserId(20));
                }
                newUser.getTeams().add(team);
                usersList.set(i, newUser);
            }
        }


        TeamEntity teamEntity = modelMapper.map(team, TeamEntity.class);
        String publicTeamId = utils.generateTeamId(20);
        teamEntity.setTeamId(publicTeamId);


        TeamEntity storedTeamDetails = teamRepo.save(teamEntity);

        if (teamEntity.getUsers() != null) {

            for (UserEntity user : teamEntity.getUsers()) {
                user.addTeam(teamEntity);
                userRepo.save(user);
            }
        }

        if (teamEntity.getTasks() != null) {
            for (TaskEntity task : teamEntity.getTasks()) {
                taskRepo.save(task);
            }
        }

        return modelMapper.map(storedTeamDetails, TeamDto.class);

    }

    @Override
    public TeamDto getTeamByTeamId(String teamId) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new ModelMapper().map(teamEntity, TeamDto.class);
    }

    @Override
    public List<TeamDto> getTeams(int page, int limit) {
        List<TeamDto> returnVal = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        if (page > 0) page -= 1;

        Pageable pageable = PageRequest.of(page, limit);
        Page<TeamEntity> teamsPage = teamRepo.findAll(pageable);
        List<TeamEntity> teams = teamsPage.getContent();

        for (TeamEntity teamEntity : teams) {
            TeamDto teamDto = modelMapper.map(teamEntity, TeamDto.class);
            returnVal.add(teamDto);

        }
        return returnVal;
    }

    @Override
    public List<TeamDto> getTeamsByUserId(String userId) {
        List<TeamDto> returnVal = new ArrayList<>();
        Iterable<TeamEntity> teamEntityList = teamRepo.findByUsers_UserId(userId);
        if (teamEntityList == null)
            return returnVal;

        ModelMapper modelMapper = new ModelMapper();

        for (TeamEntity team : teamEntityList) {
            returnVal.add(modelMapper.map(team, TeamDto.class));
        }

        return returnVal;
    }

    @Override
    public TeamDto updateTeam(String teamId, TeamDto team) {
        if(team.getName() == null) throw  new ServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());


        // check if for all tasks , the user is assigned to the team
        List<TaskDto> tasks = team.getTasks();
        if(tasks != null){
            List<UserDto> users = team.getUsers();
            for(TaskDto task: tasks){
                if(!checkIfTaskIsValid(taskRepo.findByTaskId(task.getTaskId()), users))
                    throw new ServiceException(ErrorMessages.TASK_IS_ASSIGNED_TO_WRONG_USER.getErrorMessage());
            }
        }

        teamEntity.setName(team.getName());
        updateUsers(team, teamEntity);
        updateTasks(team, teamEntity);

        try {
            TeamEntity updatedTeam = teamRepo.save(teamEntity);
            return modelMapper.map(updatedTeam, TeamDto.class);
        }catch (Exception e){
            throw new ServiceException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());
        }
    }

    @Override
    public void deleteTeam(String teamId) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            throw new ServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        teamEntity.removeAllUsers();
        teamEntity.removeAllTasks();

        try {
            teamRepo.delete(teamEntity);
        }catch (Exception e){
            throw new ServiceException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        }

    }


    private void updateUsers(TeamDto updatedTeam, TeamEntity teamToUpdate) {
        List<UserDto> newUsers = updatedTeam.getUsers();
        if (newUsers != null) {
            teamToUpdate.removeAllUsers();

            for (UserDto userDto : newUsers) {
                UserEntity userEntity = userRepo.findByUserId(userDto.getUserId());
                teamToUpdate.addUser(userEntity);
            }

        } else {
            // team has an assigned users
            if (teamToUpdate.getUsers() != null) {
                teamToUpdate.removeAllUsers();
            }
        }
    }

    private void updateTasks(TeamDto updatedTeam, TeamEntity teamToUpdate) {
        List<TaskDto> newTasks = updatedTeam.getTasks();
        if (newTasks != null) {
            teamToUpdate.removeAllTasks();

            for (TaskDto taskDto : newTasks) {
                TaskEntity taskEntity = taskRepo.findByTaskId(taskDto.getTaskId());
                teamToUpdate.addTask(taskEntity);
            }

        } else {
            // team has an assigned tasks
            if (teamToUpdate.getTasks() != null) {
                teamToUpdate.removeAllTasks();
            }
        }
    }

    private boolean checkIfTaskIsValid(TaskEntity taskFromRepo, List<UserDto> users) {
        if(taskFromRepo != null && taskFromRepo.getUserDetails() != null){
            ModelMapper modelMapper = new ModelMapper();
            TaskDto taskToCheck = modelMapper.map(taskFromRepo, TaskDto.class);
            if(users != null){
                for(UserDto user: users){
                    if(user.getUserId().equals(taskToCheck.getUserDetails().getUserId()))
                        return true;
                }
                return false;
            }
            else return taskToCheck.getUserDetails() == null;
        }
        return true;
    }
}
