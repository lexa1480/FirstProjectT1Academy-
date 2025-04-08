package ru.T1Academy.FirstProject.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.T1Academy.FirstProject.enums.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusChangeEvent {
    private Long id;
    private TaskStatus taskStatus;
}
