package com.example.demo.repositories;

import com.example.demo.entities.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskConfigRepository extends JpaRepository<TaskConfig, UUID> {
}
