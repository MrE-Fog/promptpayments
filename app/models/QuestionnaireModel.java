package models;

import utils.IntegerConverter;

import java.util.*;

/**
 * Created by daniel.rothig on 09/12/2016.
 *
 * Represents a workflow that provides an Answer based on a series of Questions
 */
public class QuestionnaireModel {

    private List<Question> questions;

    public QuestionnaireModel(List<Question> qs) {
        questions = qs;
    }

    public static QuestionnaireModel withAnswers(Map<String, String> answers) {
        QuestionnaireModel m = makeBlank();
        for (int i = 0; i < m.questions.size(); i++) {
            Integer answer = IntegerConverter.tryConvertInt(answers.getOrDefault("q" + i, null));
            if (answer != null) m.questions.get(i).setAnswer(answer);
        }
        return m;
    }
    public static QuestionnaireModel makeBlank() {
        List<Question> qs = new ArrayList<>();
        qs.add(Question.yesNo("Is your annual turnover X?", null));
        qs.add(Question.yesNo("Are your annual sales more than Y?", null));
        qs.add(Question.yesNo("Does your business have more than 250 employees?", null));
        qs.add(Question.yesNo("Is your business a subsidiary to a parent company?", null));
        qs.add(Question.yesNo("Is your business an LLC", null));

        return new QuestionnaireModel(qs);
    }

    public static class Question{
        private final String questionText;
        private final String hintText;
        private final List<String> questionOptions;
        private int answer;

        private Question(String questionText, String hintText, List<String> questionOptions) {
            this.questionText = questionText;
            this.hintText = hintText;
            this.questionOptions = Collections.unmodifiableList(questionOptions);
        }

        private static Question yesNo(String questionText, String hintText) {
            return new Question(questionText, hintText, Arrays.asList("Yes", "No"));
        }

        private void setAnswer(int answer) {
            if (answer < 0 || answer >= questionOptions.size()) {
                throw new IllegalArgumentException("invalid answer option");
            }
            this.answer = answer;
        }
    }
}
