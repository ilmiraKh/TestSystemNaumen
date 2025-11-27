package ru.khamitova.TestSystemNaumen.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "topics")
@Getter @Setter
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "{topic.name.notBlank}")
    @Size(max = 100, message = "{topic.name.size}")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "{topic.description.size}")
    private String description;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Test> tests;
}
