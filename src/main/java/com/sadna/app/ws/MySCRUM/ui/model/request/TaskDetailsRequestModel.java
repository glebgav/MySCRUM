package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDetailsRequestModel {
    private Long id;
    private String taskId;
    private String title;
    private int status;
    private String description;
    private UserRequestModel userDetails;
    private TeamRequestModel teamDetails;
}
