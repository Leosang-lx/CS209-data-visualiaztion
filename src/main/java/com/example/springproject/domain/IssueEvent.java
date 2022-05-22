package com.example.springproject.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IssueEvent {
    @Id
    private long id;
    private int issue_id;
    private int repos_id;
    private String event;
    private String created_at;

//    public IssueEvent(long id, int issue_id, int repos_id, String event, String created_at) {
//        this.id = id;
//        this.issue_id = issue_id;
//        this.repos_id = repos_id;
//        this.event = event;
//        this.created_at = created_at;
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(int issue_id) {
        this.issue_id = issue_id;
    }

    public int getRepos_id() {
        return repos_id;
    }

    public void setRepos_id(int repos_id) {
        this.repos_id = repos_id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
