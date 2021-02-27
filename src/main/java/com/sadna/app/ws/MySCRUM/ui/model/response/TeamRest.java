package com.sadna.app.ws.MySCRUM.ui.model.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamRest {
    private String teamId;
    private String name;
    private List<TaskRest> tasks;
    private List<UserFromTeamRest> users;
}
