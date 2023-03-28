package com.example.ArtShare.Models.Requests;

import com.example.ArtShare.Models.Tag;

import java.util.ArrayList;

public class UpdateContentRequest {
    public int id;
    public String title;
    public String description;
    public ArrayList<Tag> tags;

    public UpdateContentRequest() {
        tags = new ArrayList<>();
    }
}
