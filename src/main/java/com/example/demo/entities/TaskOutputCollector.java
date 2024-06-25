package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskOutputCollector {
    @EmbeddedId
    private TaskOutputCollectorId id;

    @ManyToOne(optional = false)
    @MapsId("taskOutputId")
    @JsonBackReference("taskOutputCollectors")
    private TaskOutput taskOutput;

    @ManyToOne(optional = false)
    @MapsId("basketId")
    private Basket basket;

    private boolean replaceContents;

    public TaskOutputCollector(TaskOutput taskOutput, Basket basket, boolean replaceContents) {
        this.taskOutput = taskOutput;
        this.basket = basket;
        this.replaceContents = replaceContents;
        this.id = new TaskOutputCollectorId(taskOutput.getId(), basket.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskOutputCollector that = (TaskOutputCollector) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TaskOutputCollectorId {
        private UUID taskOutputId;
        private UUID basketId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskOutputCollectorId that = (TaskOutputCollectorId) o;
            return Objects.equals(taskOutputId, that.taskOutputId) && Objects.equals(basketId, that.basketId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(taskOutputId, basketId);
        }
    }
}
