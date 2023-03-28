package com.example.ArtShare.Models.Requests;

public class CreateCommentReportRequest {
    public int CommentId;

    public CreateCommentReportRequest(int commentId) {
        CommentId = commentId;
    }
}
