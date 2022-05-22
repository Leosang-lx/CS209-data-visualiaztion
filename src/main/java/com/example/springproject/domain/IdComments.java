package com.example.springproject.domain;

public class IdComments {
    private int id;
    private int comments;
    public IdComments(int a, int b){
        id = a;
        comments = b;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
