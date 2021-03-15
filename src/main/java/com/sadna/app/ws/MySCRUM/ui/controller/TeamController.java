package com.sadna.app.ws.MySCRUM.ui.controller;

import com.sadna.app.ws.MySCRUM.service.TaskService;
import com.sadna.app.ws.MySCRUM.service.TeamService;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;
import com.sadna.app.ws.MySCRUM.ui.model.request.TeamDetailsRequestModel;
import com.sadna.app.ws.MySCRUM.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("teams")
public class TeamController {

    @Autowired
    TaskService taskService;

    @Autowired
    TeamService teamService;


    @PostMapping
    public TeamRest createTeam(@RequestBody TeamDetailsRequestModel teamDetails)
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        TeamDto teamDto = modelMapper.map(teamDetails,TeamDto.class);
        TeamDto createdTeam = teamService.createTeam(teamDto);

        return modelMapper.map(createdTeam,TeamRest.class);

    }

    @GetMapping(path = "/{id}")
    public TeamRest getTeam(@PathVariable String id)
    {
        TeamDto teamDto = teamService.getTeamByTeamId(id);

        return new ModelMapper().map(teamDto,TeamRest.class);

    }

    @GetMapping
    public List<TeamRest> getTeams(@RequestParam(value = "page", defaultValue = "0") int page
            , @RequestParam(value = "limit", defaultValue = "50") int limit){
        List<TeamRest> returnVal = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        List<TeamDto> teamsDtoList = teamService.getTeams(page, limit);

        for(TeamDto teamDto: teamsDtoList)
        {
            TeamRest teamModel = modelMapper.map(teamDto,TeamRest.class);
            returnVal.add(teamModel);

        }
        return  returnVal;
    }

    @GetMapping(path = "/{id}/tasks")
    public List<TaskRest> getTeamTasks(@PathVariable String id)
    {
        ModelMapper modelMapper = new ModelMapper();
        List<TaskRest> returnVal = new ArrayList<>();

        List<TaskDto> taskDtoList = taskService.getTasksByTeamId(id);

        if(taskDtoList != null && !taskDtoList.isEmpty()){
            Type listType = new TypeToken<List<TaskRest>>(){}.getType();
            returnVal = modelMapper.map(taskDtoList,listType);
        }

        return returnVal;

    }

    @GetMapping(path = "/{teamId}/tasks/{taskId}")
    public TaskRest getUserTask(@PathVariable String teamId, @PathVariable String taskId)
    {
        TaskRest returnVal = null;
        if(teamService.getTeamByTeamId(teamId) != null){
            TaskDto taskDto = taskService.getTask(taskId);
            if(taskDto != null){
                returnVal = new ModelMapper().map(taskDto,TaskRest.class);
            }
        }
        return returnVal;

    }

    @PutMapping(path = "/{id}")
    public TeamRest updateTeam(@PathVariable String id, @RequestBody TeamDetailsRequestModel teamDetails)
    {
        ModelMapper modelMapper = new ModelMapper();

        TeamDto teamDto = modelMapper.map(teamDetails,TeamDto.class);
        TeamDto createdTeam = teamService.updateTeam(id, teamDto);

        return modelMapper.map(createdTeam,TeamRest.class);

    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnVal = new OperationStatusModel();

        teamService.deleteUser(id);
        returnVal.setOperationName("Delete");
        returnVal.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnVal;

    }


}
