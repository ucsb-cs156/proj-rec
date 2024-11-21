package edu.ucsb.cs156.rec.entities; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommendationrequest")
public class RecommendationRequest {
    private String requesterEmail;
    private String requesterName;
    private String professorEmail;
    private String professorName;
    private String requestType;
    private String details;
    private LocalDateTime submissionDate;
    private LocalDateTime completionDate;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}