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

        qs.add(0, new Question("q0", "What type of entity is your business?", null, Arrays.asList("PLC", "Limited Liability Company", "Limited Liability Partnership", "Partnership", "Sole trader", "Other"),false));
        qs.add(1, new Question("q1", "Where is your business registered?", null, Arrays.asList("Inside the UK", "Outside the UK"),false));

        //"normal" questions
        qs.add(2, Question.yesNo("q2","On either of the last two balance sheet dates, did the company have an aggregate turnover of at least XXX", netGrossHint));
        qs.add(3, Question.yesNo("q3","On either of the last two balance sheet dates, did the company have an balance sheet total of XXX?", netGrossHint));
        qs.add(4, Question.yesNo("q4","On either of the last two balance sheet dates, did the company have a workforce of at least 250?", null));

        qs.add(5, new Question("q5", "Which of the following best describe your busines?", null, Arrays.asList("My business is part of a group but doesn't have subsidiaries", "My business is part of a group and does have subsidiaries", "My business is not part of a group"),false));

        //"parent company" questions
        qs.add(6, Question.yesNo("q6","On either of the last two balance sheet dates, did the group have an aggregate turnover of at least £36 million net or £43.2 million gross?", netGrossHint));
        qs.add(7, Question.yesNo("q7","On either of the last two balance sheet dates, did the group have an aggregate balance sheet total of £18 million net or £21.6 million gross?", netGrossHint));
        qs.add(8, Question.yesNo("q8","On either of the last two balance sheet dates, did the parent company have an aggregate workforce of at least 250?", null));

        return new QuestionnaireModel(qs);
    }

    public Answer getAnswer() {
        //disqualifiers
        int q1 = questions.get(0).answer;
        if(q1 == 3) return Answer.no("Partnerships (non-LLP) are not required to report");
        if(q1 == 4) return Answer.no("Sole traders are not required to report");
        if(q1 == 5) return Answer.no("Your type of business is not required to report");

        int q2 = questions.get(1).answer;
        if (q2 == 1) return Answer.no("Only companies within the UK are required to report");

        long companyNos = questions.subList(2,5).stream().filter(x -> x.answer == 1).count();
        if (companyNos >= 2) return Answer.no("Based on the answers you have provided, your company's size is below the thresholds of duty to report");

        long parentCompanyNos = questions.subList(6,9).stream().filter(x -> x.answer == 1).count();
        if (parentCompanyNos >= 2) return Answer.no("Based on the answers you have provided, your group's size is below the thresholds of duty to report");

        long companyYesses = questions.subList(2,5).stream().filter(x -> x.answer == 0).count();
        long parentCompanyYesses = questions.subList(6,9).stream().filter(x -> x.answer == 0).count();

        switch (questions.get(5).answer) {
            case -1: return null;
            case  0: return companyYesses >= 2 ? Answer.yes(true) : null;
            case  1: return parentCompanyYesses >= 2 && companyYesses >= 2 ? Answer.yes(true) : null;
            case  2: return companyYesses >= 2 ? Answer.yes(false) : null;
            default: return null;
        }
    }

    public Question getNextQuestion() {
        if (getAnswer() != null) return null;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (i == 4 && questions.get(3).answer == 0 && questions.get(2).answer == 0) continue;
            if (i == 8 && questions.get(7).answer == 0 && questions.get(6).answer == 0) continue;
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
