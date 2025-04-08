package ru.T1Academy.FirstProject.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.T1Academy.FirstProject.enums.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private TaskStatus taskStatus;
    private String title;
    private String description;
    private Long userId;
}