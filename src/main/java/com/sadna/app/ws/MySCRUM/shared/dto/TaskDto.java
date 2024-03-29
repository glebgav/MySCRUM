package com.sadna.app.ws.MySCRUM.shared.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * Data transfer object for Task ( link between Entity models and Rest models)
 */
@Getter
@Setter
public class TaskDto implements Serializable {
    private long id;
    private String taskId;
    private String title;
    private int status;
    private String description;
    private UserDto userDetails;
    private TeamDto teamDetails;

}
