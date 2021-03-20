package com.sadna.app.ws.MySCRUM.service;

import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;

import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskDto taskDto);

    List<TaskDto> getTasksByUserId(String userId);

    TaskDto getTask(String taskId);

    List<TaskDto> getTasksByTeamId(String teamId);

    TaskDto updateTask(String taskId, TaskDto taskDto);

    void deleteTask(String taskId);
}
