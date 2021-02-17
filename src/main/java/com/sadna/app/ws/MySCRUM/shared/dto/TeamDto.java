package com.sadna.app.ws.MySCRUM.shared.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class TeamDto implements Serializable {
    private long id;
    private String teamId;
    private String name;

}
