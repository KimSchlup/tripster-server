package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;


import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "point_of_interest_comment")

public class PointOfInterestComment implements Serializable{


    @Id
    @GeneratedValue
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "poi_id", nullable = false)

    private PointOfInterest poi;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDate creationDate;

public PointOfInterest getPoi() {
        return poi;
    }

    public void setPoi(PointOfInterest poi) {
        this.poi = poi;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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