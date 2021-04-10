package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response model for Task
 */
@Getter
@Setter
public class TaskRest {
    private String taskId;
    private String title;
    private int status;
    private String description;
    private UserFromTaskRest userDetails;
    private TeamFromTaskRest teamDetails;

}
