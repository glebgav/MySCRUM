package com.sadna.app.ws.MySCRUM.io.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import javax.persistence.*;
import java.io.Serializable;


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

    @ManyToOne()
    @JoinColumn(name = "teams_id")
    private TeamEntity teamDetails;

    public void removeTeam(TeamEntity team) {
        teamDetails = null;
        team.getTasks().remove(this);
    }

    public void addTeam(TeamEntity team) {
        teamDetails = team;
        team.getTasks().add(this);
    }

    public void removeUser(UserEntity user) {
        userDetails = null;
        user.getTasks().remove(this);
    }

    public void addUser(UserEntity user) {
        userDetails = user;
        user.getTasks().add(this);
    }
}
