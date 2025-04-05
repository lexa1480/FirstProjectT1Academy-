package ru.T1Academy.FirstProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.T1Academy.FirstProject.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}