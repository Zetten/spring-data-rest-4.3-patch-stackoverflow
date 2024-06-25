package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskOutput {
    @Id
    @GeneratedValue
    private UUID id;

    private String outputIdentifier;

    @ManyToOne(optional = false, fetch= FetchType.LAZY)
    @JsonBackReference("taskConfigOutputs")
    private TaskConfig taskConfig;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskOutput", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("taskOutputCollectors")
    private Set<TaskOutputCollector> collectors = new HashSet<>();

    public void addCollector(Basket basket, boolean replaceContents) {
        collectors.add(new TaskOutputCollector(this, basket, replaceContents));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskOutput that = (TaskOutput) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
