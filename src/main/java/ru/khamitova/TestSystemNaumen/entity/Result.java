package ru.khamitova.TestSystemNaumen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.khamitova.TestSystemNaumen.entity.enums.ResultStatus;

import java.util.List;

@Entity
@Table(name="results")
@Getter @Setter
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ResultStatus status;

    @Column
    private Double score;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;

}
