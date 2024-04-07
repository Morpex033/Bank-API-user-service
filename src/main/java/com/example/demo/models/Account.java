package com.example.demo.models;

import com.example.demo.models.enums.account.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Account{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    @Column
    private double balance;

    @Column
    private Status status;

    @Column
    private Date createdAt;

    @Column
    private Date lastActive;
}
