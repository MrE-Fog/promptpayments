package models;

import utils.IntegerConverter;

import java.util.*;

/**
 * Created by daniel.rothig on 09/12/2016.
 *
 * Represents a workflow that provides an Answer based on a series of Questions
 */
public class QuestionnaireModel {

    public List<Question> questions;

    public QuestionnaireModel(List<Question> qs) {
        questions = qs;
    }

    public static QuestionnaireModel withAnswers(Map<String, String[]> answers) {
        QuestionnaireModel m = makeBlank();
        for (int i = 0; i < m.questions.size(); i++) {
            String[] arrayOfAnswers = answers.getOrDefault("q" + i, null);
            if (arrayOfAnswers == null) continue;
            Integer answer = IntegerConverter.tryConvertInt(arrayOfAnswers[arrayOfAnswers.length-1]);
            if (answer != null) m.questions.get(i).setAnswer(answer);
        }
        return m;
    }
    public static QuestionnaireModel makeBlank() {
        List<Question> qs = new ArrayList<>();
        String netGrossHint = "'Net' here means after any set-offs and other adjustments to exclude group transactions. 'Gross' means without those set-offs and adjustments.";

        String businessOnlyHint = "If your business is part of a group, your answers must be for your business on its own. Every business within the group will need to do this individually";

        qs.add(0, Question.yesNo("q0", "Is your business a company or Limited Liability Partnership incorporated in the UK?", null));

        //"normal" questions
        qs.add(1, Question.yesNo("q1","Did your business have a turnover of more than £36 million on its last 2 balance sheet dates?", businessOnlyHint));
        qs.add(2, Question.yesNo("q2","Did your business have a balance sheet total greater than £18 million at its last 2 financial year ends?", businessOnlyHint));
        qs.add(3, Question.yesNo("q3","Did your business have an average of at least 250 employees during both of its last 2 financial years?", businessOnlyHint));

        qs.add(4, Question.yesNo("q4", "Does your business have subsidiaries?", null));

        //"subsidiaries" questions
        qs.add(5, Question.yesNo("q5","Did you and your subsidiaries have an total turnover of at least £36 million net or £43.2 million gross on both of the last 2 balance sheet dates?", netGrossHint));
        qs.add(6, Question.yesNo("q6","Did you and your subsidiaries have a combined balance sheet total of £18 million net or £21.6 million gross on both of the last 2 balance sheet dates?", netGrossHint));
        qs.add(7, Question.yesNo("q7","Did the you and your subsidiaries have a combined workforce of at least 250 on both of the last 2 balance sheet dates?", null));

        return new QuestionnaireModel(qs);
    }

    public Answer getAnswer() {
        //disqualifiers
        int q1 = questions.get(0).answer;
        if(q1 == 1) return Answer.no(null);

        long companyNos = questions.subList(1,4).stream().filter(x -> x.answer == 1).count();
        if (companyNos >= 2) return Answer.no("Your business isn’t large enough to have to report. You should check once a year to see if this has changed.");

        long parentCompanyNos = questions.subList(5,8).stream().filter(x -> x.answer == 1).count();
        if (parentCompanyNos >= 2) return Answer.no("Your group isn’t large enough to have to report. You should check once a year to see if this has changed.");

        long companyYesses = questions.subList(1,4).stream().filter(x -> x.answer == 0).count();
        long parentCompanyYesses = questions.subList(5,8).stream().filter(x -> x.answer == 0).count();

        switch (questions.get(4).answer) {
            case  0: return parentCompanyYesses >= 2 && companyYesses >= 2 ? Answer.yes(true) : null;
            case  1: return companyYesses >= 2 ? Answer.yes(false) : null;
            default: return null;
        }
    }

    public Question getNextQuestion() {
        if (getAnswer() != null) return null;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (i == 3 && questions.get(2).answer == 0 && questions.get(1).answer == 0) continue;
            if (i == 7 && questions.get(6).answer == 0 && questions.get(5).answer == 0) continue;
            if (!question.hasAnswer()) return question;
        }

        return null; //todo: error handling
    }

    public static class Question{
        public final String questionText;
        public final String hintText;
        public final List<String> questionOptions;
        public final String id;
        public final boolean inline;
        private int answer = -1;

        private Question(String id, String questionText, String hintText, List<String> questionOptions, boolean inline) {
            this.id = id;
            this.questionText = questionText;
            this.hintText = hintText;
            this.questionOptions = Collections.unmodifiableList(questionOptions);
            this.inline = inline;
        }

        private static Question yesNo(String id, String questionText, String hintText) {
            return new Question(id, questionText, hintText, Arrays.asList("Yes", "No"), true);
        }

        private void setAnswer(int answer) {
            if (answer < -1 || answer >= questionOptions.size()) {
                throw new IllegalArgumentException("invalid answer option");
            }
            this.answer = answer;
        }

        public int getAnswer() {return answer;}

        public boolean hasAnswer() { return answer > -1; }
    }

    public static class Answer {
        public final boolean shouldFile;
        public final boolean showGroupGuidance;
        public final String reason;

        public Answer(boolean shouldFile, boolean showGroupGuidance, String reason) {
            this.shouldFile = shouldFile;
            this.showGroupGuidance = showGroupGuidance;
            this.reason = reason;
        }

        public static Answer no(String reason) {
            return new Answer(false, false, reason);
        }

        public static Answer yes(boolean showGroupGuidance) {
            return new Answer(true, showGroupGuidance, null);
        }
    }
}
