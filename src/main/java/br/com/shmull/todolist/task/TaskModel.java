package br.com.shmull.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
  @Id
  @GeneratedValue(generator = "uuid4")
  private UUID id;
  
  @Column(length = 50)
  private String title;
  private String description;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private String priority;
  
  private UUID idUser;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public void setTitle(String title) throws Exception {
    if (title == null || title.trim().length() == 0) {
      throw new Exception("Title is required");
    }
    if (title.trim().length() < 3) {
      throw new Exception("Title must be at least 3 characters");
    }
    if (title.trim().length() > 50) {
      throw new Exception("Title must be at most 50 characters");
    }
    this.title = title;
  }

}
