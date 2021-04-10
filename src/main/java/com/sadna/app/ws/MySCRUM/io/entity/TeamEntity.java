package com.sadna.app.ws.MySCRUM.io.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Team Entity (ORM)
 */
@Entity(name="teams")
@Getter
@Setter
public class TeamEntity implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String teamId;

    @Column(nullable = false,length = 50)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teamDetails")
    private List<TaskEntity> tasks;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teams")
    private List<UserEntity> users;

    public void addUser(UserEntity user) {
        users.add(user);
        user.getTeams().add(this);
    }

    public void removeUser(UserEntity user) {
        users.remove(user);
        user.getTeams().remove(this);
    }

    public void removeAllUsers() {
        if(users != null){
            while (users.size() != 0){
                users.get(0).removeTeam(this);
            }
            users.clear();
        }
    }

    public void addTask(TaskEntity task){
        tasks.add(task);
        task.setTeamDetails(this);
    }

    public void removeTask(TaskEntity task){
        tasks.remove(task);
        task.setTeamDetails(null);
    }

    public void removeAllTasks(){
        if(tasks != null){
            while (tasks.size() != 0){
                tasks.get(0).removeTeam(this);
            }
            tasks.clear();
        }
    }

}
