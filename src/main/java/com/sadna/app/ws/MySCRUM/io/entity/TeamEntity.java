package com.sadna.app.ws.MySCRUM.io.entity;

import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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

    @OneToMany(mappedBy = "teamDetails", cascade = CascadeType.ALL)
    private List<TaskEntity> tasks;

/*    @ManyToMany(mappedBy = "teams", cascade = CascadeType.ALL)
    private List<UserEntity> users;*/


}
