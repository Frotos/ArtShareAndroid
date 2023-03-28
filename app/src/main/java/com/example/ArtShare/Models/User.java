package com.example.ArtShare.Models;

public class User {
    public int id;
    public String username;
    public String email;
    public String password;
    public Avatar avatar;
    public int followersCount;
    public int followingsCount;
    public int postsCount;
    public boolean isFollowed;

    public User() {
    }

    public User(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
