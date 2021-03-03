package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImp implements TeamService {

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    Utils utils;

    @Override
    public TeamDto createTeam(TeamDto team) {
        if(team.getTasks() != null) {
            for (TaskDto task : team.getTasks()) {
                task.setTeamDetails(team);
                task.setTaskId(utils.generateTaskId(20));
            }
        }
        if(team.getUsers() != null) {
            for (UserDto user : team.getUsers()) {
                user.getTeams().add(team);
                user.setUserId(utils.generateTaskId(20));
            }
        }
        ModelMapper modelMapper = new ModelMapper();
        TeamEntity teamEntity = modelMapper.map(team, TeamEntity.class);

        String publicUserId = utils.generateUserId(20);
        teamEntity.setTeamId(publicUserId);

        TeamEntity storedTeamDetails = teamRepo.save(teamEntity);

        return modelMapper.map(storedTeamDetails, TeamDto.class);

    }

    @Override
    public TeamDto getTeamByTeamId(String teamId) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if(teamEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new ModelMapper().map(teamEntity, TeamDto.class);
    }

    @Override
    public List<TeamDto> getTeams(int page, int limit) {
        List<TeamDto> returnVal = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        if(page > 0) page-=1;

        Pageable pageable =  PageRequest.of(page,limit);
        Page<TeamEntity> teamsPage = teamRepo.findAll(pageable);
        List<TeamEntity> teams = teamsPage.getContent();

        for(TeamEntity teamEntity: teams)
        {
            TeamDto teamDto = modelMapper.map(teamEntity, TeamDto.class);
            returnVal.add(teamDto);

        }
        return returnVal;
    }

    @Override
    public TeamDto updateTeam(String teamId, TeamDto team) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if(teamEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        teamEntity.setName(team.getName());
        Type listType = new TypeToken<List<TaskEntity>>(){}.getType();
        teamEntity.setTasks(modelMapper.map(team.getTasks(),listType));

        TeamEntity updatedTeam = teamRepo.save(teamEntity);
        return modelMapper.map(updatedTeam, TeamDto.class);
    }

    @Override
    public List<TeamDto> getTeamsByUserId(String userId) {
        List<TeamDto> returnVal = new ArrayList<>();
        Iterable<TeamEntity> teamEntityList = teamRepo.findByUsers_UserId(userId);
        if(teamEntityList==null)
            return returnVal;

        ModelMapper modelMapper = new ModelMapper();

        for(TeamEntity team: teamEntityList){
            returnVal.add(modelMapper.map(team,TeamDto.class));
        }

        return returnVal;
    }


    @Override
    public void deleteUser(String teamId) {
        TeamEntity teamEntity = teamRepo.findByTeamId(teamId);
        if(teamEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        teamRepo.delete(teamEntity);
    }
}
