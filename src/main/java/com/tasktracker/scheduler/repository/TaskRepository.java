package com.tasktracker.scheduler.repository;

import com.tasktracker.scheduler.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDoneTrueAndUpdatedAtBetween(long userId, LocalDateTime from, LocalDateTime to
    );

    List<Task> findTop5ByUserIdAndDoneFalse(long userId);
}
