package com.solo83.weatherapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Entity
@Table(name = "Sessions")
public class UserSession {
    @Id
    @Column(name = "id", unique = true)
    private String id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @Column(name = "expiresAt")
    private LocalDateTime expiresAt;

    public UserSession() {

    }
}