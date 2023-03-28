package com.example.ArtShare.Models.Requests;

public class UploadCommentRequest {
    public int id;
    public int ContentId;
    public String CommentText;

    public UploadCommentRequest(int contentId, String commentText) {
        ContentId = contentId;
        CommentText = commentText;
    }
}
