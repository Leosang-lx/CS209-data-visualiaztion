package com.example.springproject.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Issue {
    @Id
    private int id;
    private int repos_id;
    private int number;
    private String state;
    private String created_at;
    private String updated_at;
    private String closed_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepos_id() {
        return repos_id;
    }

    public void setRepos_id(int repos_id) {
        this.repos_id = repos_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }
}
