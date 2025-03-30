package ru.T1Academy.FirstProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest
{
    @NotBlank(message = "Название не может быть пустым.")
    @Size(min = 5, max = 255, message = "Длинна названия не может быть меньше 5 и больше 255.")
    private String title;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(min = 5, max = 1000, message = "Длинна описания не может быть меньше 5 и больше 1000.")
    private String description;

    @NotNull(message = "У задачи не может отсутствовать пользователь.")
    @Positive(message = "Пользователь не может иметь отрицательный id.")
    private Long userId;
}
