package ru.T1Academy.FirstProject.mapper;

import org.mapstruct.Mapper;
import ru.T1Academy.FirstProject.dto.event.TaskStatusChangeEvent;
import ru.T1Academy.FirstProject.dto.request.TaskCreateRequest;
import ru.T1Academy.FirstProject.dto.request.TaskUpdateRequest;
import ru.T1Academy.FirstProject.dto.response.TaskResponse;
import ru.T1Academy.FirstProject.model.Task;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toTask(TaskCreateRequest taskCreateRequest);
    Task toTask(TaskUpdateRequest taskUpdateRequest);

    TaskResponse toTaskResponse(Task task);
    List<TaskResponse> toTaskResponseList(List<Task> taskList);
    TaskStatusChangeEvent toTaskStatusChangeEvent(Task task);
}