package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;
/**
 * Request model for Task (without users and teams)
 */
@Getter
@Setter
public class TaskRequestModel {
    private String taskId;
    private String title;
    private int status;
    private String description;
}
