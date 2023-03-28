package com.example.ArtShare.Models;

import java.util.Objects;

public class Comment {
    public int id;
    public String text;
    public User user;
    public int contentId;
    public String publishDate;
    public int likesCount;
    public boolean isLiked;
    public int dislikesCount;
    public boolean isDisliked;
    public boolean uploadedByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, user, contentId, publishDate, likesCount, isLiked, dislikesCount, isDisliked);
    }
}
