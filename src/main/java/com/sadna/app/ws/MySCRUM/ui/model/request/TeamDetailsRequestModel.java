package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * Request model for Team details
 */
@Getter
@Setter
public class TeamDetailsRequestModel {
    private String teamId;
    private String name;
    private List<TaskRequestModel> tasks;
    private List<UserRequestModel> users;
}
