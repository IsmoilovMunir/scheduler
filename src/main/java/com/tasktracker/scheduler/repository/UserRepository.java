package com.tasktracker.scheduler.repository;

import com.tasktracker.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
