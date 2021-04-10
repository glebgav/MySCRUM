package com.sadna.app.ws.MySCRUM.io.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * User Entity (ORM)
 */
@Entity(name="users")
@Getter
@Setter
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;

    @Column(nullable = false,length = 120,unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    @Column(nullable = false)
    private Boolean isManager;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userDetails")
    private List<TaskEntity> tasks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_team",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "teams_id"))
    private List<TeamEntity> teams;


    public void addTeam(TeamEntity team) {
        teams.add(team);
        team.getUsers().add(this);
    }

    public void removeTeam(TeamEntity team) {
        teams.remove(team);
        team.getUsers().remove(this);
    }

    public void removeAllTeams() {
        if(teams != null){
            while (teams.size() != 0){
                teams.get(0).removeUser(this);
            }
            teams.clear();
        }
    }

    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setUserDetails(this);
    }

    public void removeTask(TaskEntity task) {
        tasks.remove(task);
        task.setUserDetails(null);
    }

    public void removeAllTasks(){
        if(tasks != null){
            while (tasks.size() != 0){
                tasks.get(0).removeUser(this);
            }
            tasks.clear();
        }
    }

}
