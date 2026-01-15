package com.mealtracker.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "meals")
@NamedEntityGraph(name = "Meal.consumer", attributeNodes = @NamedAttributeNode("consumer"))
@Getter
@Setter
public class Meal implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "consumed_date", nullable = false)
    private LocalDate consumedDate;

    @Column(name = "consumed_time", nullable = false)
    private LocalTime consumedTime;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private User consumer;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Override
    public User getOwner() {
        return consumer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Meal meal = (Meal) o;
        return id != null && Objects.equals(id, meal.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", consumedDate=" + consumedDate +
                ", consumedTime=" + consumedTime +
                ", calories=" + calories +
                ", deleted=" + deleted +
                '}';
    }
}
