package ru.T1Academy.FirstProject.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task")
@Entity
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title", nullable = false, length = 255)
    String title;

    @Column(name = "description", nullable = false, length = 1000)
    String description;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT CHECK (user_id > 0)")
    Long userId;
}
