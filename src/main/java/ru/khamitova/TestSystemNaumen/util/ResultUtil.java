package ru.khamitova.TestSystemNaumen.util;

import ru.khamitova.TestSystemNaumen.entity.Answer;
import ru.khamitova.TestSystemNaumen.entity.Result;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultUtil {
    public static void selectOptions(Result result){
        for (Answer answer : result.getAnswers()) {
            if (answer.getAnswer() != null && !answer.getAnswer().isBlank()) {
                switch (answer.getQuestion().getType()){
                    case CHOICE -> {
                        Set<Long> selectedIds = Arrays.stream(answer.getAnswer().split(","))
                                .map(Long::valueOf)
                                .collect(Collectors.toSet());
                        answer.setSelectedOptionIds(selectedIds);
                    }
                    case OPEN ->
                            answer.setSelectedOptionIds(Collections.emptySet());
                }
            } else {
                answer.setSelectedOptionIds(Collections.emptySet());
            }
        }
    }
}
