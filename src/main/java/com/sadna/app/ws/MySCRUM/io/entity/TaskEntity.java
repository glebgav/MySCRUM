package com.sadna.app.ws.MySCRUM.io.entity;

import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name="tasks")
@Getter
@Setter
public class TaskEntity implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String taskId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int status;

    @Column
    private String description;

    @ManyToOne()
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;

/*    @ManyToOne()
    @JoinColumn(name = "teams_id")
    private TeamEntity teamDetails;*/

}
