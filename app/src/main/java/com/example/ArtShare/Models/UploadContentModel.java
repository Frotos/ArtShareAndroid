package com.example.ArtShare.Models;

import com.example.ArtShare.Enums.UploadOperationsEnum;

import java.io.Serializable;
import java.util.List;

public class UploadContentModel implements Serializable {
    public int id;
    public ContentTypesEnum type;
    public String title;
    public String description;
    public List<Tag> tags;
    public UploadOperationsEnum operation;

    public UploadContentModel(ContentTypesEnum type, UploadOperationsEnum operation) {
        this.type = type;
        this.operation = operation;
    }

    public UploadContentModel(int id, ContentTypesEnum type, String title, String description, List<Tag> tags, UploadOperationsEnum operation) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.operation = operation;
    }
}
