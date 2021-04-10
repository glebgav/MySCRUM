package com.sadna.app.ws.MySCRUM.shared.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

/**
 * Data transfer object for Team ( link between Entity models and Rest models)
 */
@Getter
@Setter
public class TeamDto implements Serializable {
    private long id;
    private String teamId;
    private String name;
    private List<TaskDto> tasks;
    private List<UserDto> users;

}
