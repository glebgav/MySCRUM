package com.sadna.app.ws.MySCRUM.io.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;


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
    private String LastName;

    @Column(nullable = false,length = 120,unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;
}
