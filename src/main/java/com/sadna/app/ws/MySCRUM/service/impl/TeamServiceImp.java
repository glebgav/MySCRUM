package com.sadna.app.ws.MySCRUM.service.impl;

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
import com.sadna.app.ws.MySCRUM.ui.model.response.TaskRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.Task;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
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
        if (team.getTasks() != null) {
            for (TaskDto task : team.getTasks()) {
                task.setTeamDetails(team);
                task.setTaskId(utils.generateTaskId(20));
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
                    newUser.setUserId(utils.generateTaskId(20));
                }
                newUser.getTeams().add(team);
                usersList.set(i, newUser);
            }
        }

        TeamEntity teamEntity = modelMapper.map(team, TeamEntity.class);

        String publicUserId = utils.generateUserId(20);
        teamEntity.setTeamId(publicUserId);

        TeamEntity storedTeamDetails = teamRepo.save(teamEntity);

        if (teamEntity.getUsers() != null) {
            for (UserEntity user : teamEntity.getUsers()) {
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
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

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
    public TeamDto updateTeam(String teamId, TeamDto team) {
        ModelMapper modelMapper = new ModelMapper();
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());


        teamEntity.setName(team.getName());
        updateUsers(team, teamEntity);
        updateTasks(team, teamEntity);

        TeamEntity updatedTeam = teamRepo.save(teamEntity);
        return modelMapper.map(updatedTeam, TeamDto.class);
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
    public void deleteTeam(String teamId) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if (teamEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        if (teamEntity.getUsers() != null) {
            List<UserEntity> users = teamEntity.getUsers();
            while (users.size() != 0) {
                users.get(0).removeTeam(teamEntity);
            }
        }

        if (teamEntity.getTasks() != null) {
            List<TaskEntity> tasks = teamEntity.getTasks();
            while (tasks.size() != 0) {
                tasks.get(0).removeTeam(teamEntity);
            }
        }

        teamRepo.delete(teamEntity);
    }


    private void updateUsers(TeamDto updatedTeam, TeamEntity teamToUpdate) {
        if (updatedTeam.getUsers() != null) {
            teamToUpdate.removeAllUsers();

            for (UserDto userDto : updatedTeam.getUsers()) {
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
        if (updatedTeam.getTasks() != null) {
            teamToUpdate.removeAllTasks();

            for (TaskDto taskDto : updatedTeam.getTasks()) {
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
}
