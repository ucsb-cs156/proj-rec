package edu.ucsb.cs156.rec.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * This is a JPA entity that represents a Recommendation Request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommendationrequest")
@EntityListeners(AuditingEntityListener.class)
public class RecommendationRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "requester_id", referencedColumnName = "id", insertable = false, updatable = false)
  private User requester;
  private Long requester_id;

  @ManyToOne
  @JoinColumn(name = "professor_id", referencedColumnName = "id", insertable = false, updatable = false)
  private User professor;
  private Long professor_id;

  private String recommendationType;
  private String details;
  private String status;

  private LocalDateTime completionDate;
  private LocalDateTime dueDate;
  @CreatedDate
  private LocalDateTime submissionDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;
}