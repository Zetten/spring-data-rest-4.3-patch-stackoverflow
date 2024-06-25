package com.example.demo

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class TaskDefinition(
    @Id @GeneratedValue var id: UUID? = null,

    var name: String,

    @ElementCollection var inputs: List<String> = mutableListOf(),

    @ElementCollection var outputs: List<String> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TaskDefinition
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}

@Entity
class TaskConfig(
    @Id @GeneratedValue var id: UUID? = null,

    @ManyToOne(optional = false) @JoinColumn var taskDefinition: TaskDefinition,

    @ElementCollection var inputs: List<String> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskConfig", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference("taskConfigOutputs")
    var outputs: List<TaskOutput> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TaskConfig
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}

@Entity
class TaskOutput(
    @Id @GeneratedValue var id: UUID? = null,

    var outputIdentifier: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonBackReference("taskConfigOutputs")
    var taskConfig: TaskConfig,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskOutput", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference("taskOutputCollectors")
    var collectors: Set<TaskOutputCollector> = mutableSetOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TaskOutput
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    fun addCollector(basket: Basket, replaceContents: Boolean = false) {
        collectors += TaskOutputCollector(this, basket, replaceContents)
    }
}

@Entity
class TaskOutputCollector(
    @ManyToOne(optional = false)
    @MapsId("taskOutputId")
    @JsonBackReference("taskOutputCollectors")
    var taskOutput: TaskOutput,

    @ManyToOne(optional = false)
    @MapsId("basketId")
    var basket: Basket,

    var replaceContents: Boolean,

    @EmbeddedId var id: TaskOutputCollectorId = TaskOutputCollectorId(taskOutput.id, basket.id),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TaskOutputCollector
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}

@Embeddable
class TaskOutputCollectorId(var taskOutputId: UUID? = null, var basketId: UUID? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TaskOutputCollectorId
        if (taskOutputId != other.taskOutputId) return false
        if (basketId != other.basketId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = taskOutputId.hashCode()
        result = 31 * result + basketId.hashCode()
        return result
    }
}

@Entity
class Basket(
    @Id @GeneratedValue var id: UUID? = null,

    var name: String,

    @ElementCollection
    var contents: List<String> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Basket
        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}