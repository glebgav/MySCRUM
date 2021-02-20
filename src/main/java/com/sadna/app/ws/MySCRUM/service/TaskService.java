package com.sadna.app.ws.MySCRUM.service;

import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import java.util.List;

public interface TaskService {
    List<TaskDto> getTasksByUserId(String id);
    TaskDto getTask(String taskId);
}
