package com.tasktracker.scheduler.service;

import com.tasktracker.scheduler.dto.EmailEventDto;
import com.tasktracker.scheduler.entity.Task;
import com.tasktracker.scheduler.entity.User;
import com.tasktracker.scheduler.repository.TaskRepository;
import com.tasktracker.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, EmailEventDto> kafkaTemplate;


    @Scheduled(cron = "0 0 0 * * *")
    public void sendDailyReports() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = LocalDate.now().atStartOfDay();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Task> doneTask = taskRepository.findByUserIdAndDoneTrueAndUpdatedAtBetween(
                    user.getId(), startOfYesterday, endOfYesterday
            );

            List<Task> pendingTasks = taskRepository.findTop5ByUserIdAndDoneFalse(user.getId());
            String body = buildReportText(doneTask, pendingTasks);
            EmailEventDto event = new EmailEventDto(user.getEmail(), "Ежедневный отчёт", body);
            kafkaTemplate.send("EMAIL_SENDING_TASKS", event);
        }
    }

    private String buildReportText(List<Task> doneTask, List<Task> pendingTasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Выполнено вчера: ").append(doneTask.size()).append("\n");
        sb.append("\nНевыполненные задачи:\n");
        for (Task task : pendingTasks){
            sb.append("- ").append(task.getTitle()).append("\n");
        }
        return sb.toString();
    }
}
