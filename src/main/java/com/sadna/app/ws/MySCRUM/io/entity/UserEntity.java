package com.sadna.app.ws.MySCRUM.io.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


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

}
