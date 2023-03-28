package com.example.ArtShare.Models;

public class Following {
    public int id;
    public int followUserId;
    public int followingUserId;

    public Following(){}

    public Following(int followUserId, int followingUserId) {
        this.followUserId = followUserId;
        this.followingUserId = followingUserId;
    }
}
