package com.example.springproject.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserEvent {
    @Id
    private Long id;
    private String type;
    private int actor_id;
    private int repos_id;
    private String created_at;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getActor_id() {
        return actor_id;
    }

    public void setActor_id(int actor_id) {
        this.actor_id = actor_id;
    }

    public int getRepos_id() {
        return repos_id;
    }

    public void setRepos_id(int repos_id) {
        this.repos_id = repos_id;
    }
}
