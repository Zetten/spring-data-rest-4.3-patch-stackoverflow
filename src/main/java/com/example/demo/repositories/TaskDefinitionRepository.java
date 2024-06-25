package com.example.demo.repositories;

import com.example.demo.entities.TaskDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, UUID> {
}
