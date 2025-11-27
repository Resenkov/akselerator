package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Setter
    @Column(name = "description")
    private String description;

    public Role() {}
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
