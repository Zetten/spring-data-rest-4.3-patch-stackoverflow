package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskDefinitionRepository : JpaRepository<TaskDefinition, UUID>

interface TaskConfigRepository : JpaRepository<TaskConfig, UUID>

interface BasketRepository : JpaRepository<Basket, UUID>