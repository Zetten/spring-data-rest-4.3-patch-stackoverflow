package com.example.demo.entities;

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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskConfig {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private TaskDefinition taskDefinition;

    @ElementCollection
    private List<String> inputs = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("taskConfigOutputs")
    private List<TaskOutput> outputs = new ArrayList<>();

    public void addOutput(TaskOutput output) {
        output.setTaskConfig(this);
        this.outputs.add(output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskConfig that = (TaskConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
