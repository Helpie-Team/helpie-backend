package com.helpie.backend.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 계정id
    @Column(name = "username", nullable = false)
    private String username;


    public User(String username) {
        this.username = username;
    }
}
