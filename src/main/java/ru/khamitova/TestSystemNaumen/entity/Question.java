package ru.khamitova.TestSystemNaumen.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.khamitova.TestSystemNaumen.entity.enums.QuestionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter @Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{question.text.required}")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @NotNull(message = "{question.points.required}")
    @Min(value = 1, message = "{question.points.positive}")
    @Column(nullable = false)
    private Integer points;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "manual_check_required", nullable = false)
    private Boolean manualCheckRequired;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @Transient
    public int getCorrectCount() {
        if (options == null) return 0;
        return (int) options.stream().filter(o -> Boolean.TRUE.equals(o.getIsCorrect())).count();
    }
}
