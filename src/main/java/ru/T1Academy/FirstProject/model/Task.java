package ru.T1Academy.FirstProject.model;

import jakarta.persistence.*;
import lombok.*;
import ru.T1Academy.FirstProject.enums.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus taskStatus = TaskStatus.CREATED;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT CHECK (user_id > 0)")
    private Long userId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task task)) return false;
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Task: " + title + "; " + description;
    }
}