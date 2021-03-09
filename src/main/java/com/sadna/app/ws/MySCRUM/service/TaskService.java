package com.sadna.app.ws.MySCRUM.service;

import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import java.util.List;

public interface TaskService {
    List<TaskDto> getTasksByUserId(String userId);

    TaskDto getTask(String taskId);

    List<TaskDto> getTasksByTeamId(String teamId);

    TaskDto updateTask(String taskId, TaskDto taskDto);
}
