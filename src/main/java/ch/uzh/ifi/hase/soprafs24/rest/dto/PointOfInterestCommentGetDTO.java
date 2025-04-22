package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;

public class PointOfInterestCommentGetDTO {
    
    private Long commentId;
    private PointOfInterest poi;
    private Long authorId;
    private String comment;
    private LocalDate creationDate;

    
// Getter and Setter for poi
    public PointOfInterest getPoi() {
        return poi;
    }

    public void setPoi(PointOfInterest poi) {
        this.poi = poi;
    }

    // Getter and Setter for authorId
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    // Getter and Setter for comment
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Getter and Setter for creationDate
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCommentId(){
        return commentId;
    }

    public void setCommentId(Long commentId){
        this.commentId = commentId;
    }
    
}
