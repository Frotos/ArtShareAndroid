package com.example.ArtShare.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Content implements Serializable {
    public int id;
    public ContentTypesEnum type;
    public User user;
    public String title;
    public String description;
    public int likesCount;
    public boolean isLiked;
    public int dislikesCount;
    public boolean isDisliked;
    public String contentBase64;
    public boolean uploadedByUser;
    public List<Tag> tags;

    public Content() {
        tags = new ArrayList<>();
    }

    public Content(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.ArtShare.Models.Content that = (com.example.ArtShare.Models.Content) o;
        return id == that.id;
    }

    // TODO: Change fields(add all)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
