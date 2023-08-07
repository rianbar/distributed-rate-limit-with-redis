package com.example.limitation.domain.user;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private int rate_limit;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLimit() {
        return rate_limit;
    }

    public void setLimit(int limit) {
        this.rate_limit = limit;
    }
}
