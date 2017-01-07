package controllers;

import models.CalculatorModel;
import models.QuestionnaireModel;
import play.mvc.Result;
import views.html.Questionnaire.*;

/**
 * Created by daniel.rothig on 13/12/2016.
 *
 * Serves a series of questions to produce a customised result page
 */
public class Questionnaire extends PageController {
    public Result start() {
        return ok(page(start.render()));
    }

    public Result ask() {
        QuestionnaireModel model = QuestionnaireModel.withAnswers(request().queryString());
        if (model.getAnswer() != null) {
            return model.getAnswer().shouldFile
                ? redirect(withCurrentQuery(routes.Questionnaire.qualified()))
                : redirect(withCurrentQuery(routes.Questionnaire.disqualified()));
        } else {
            return ok(page(question.render(model)));
        }
    }

    public Result calculator() {
        QuestionnaireModel model = QuestionnaireModel.withAnswers(request().queryString());
        CalculatorModel calculatorModel = new CalculatorModel(
                getQueryParameter("start-year"),
                getQueryParameter("start-month"),
                getQueryParameter("start-day"),
                getQueryParameter("end-year"),
                getQueryParameter("end-month"),
                getQueryParameter("end-day"));

        if (!calculatorModel.isEmpty() && calculatorModel.isValid()) {
            return redirect(withCurrentQuery(routes.Questionnaire.answer()));
        } else {
            return ok(page(calculator.render(model, calculatorModel)));
        }
    }

    public Result disqualified() {
        QuestionnaireModel.Answer answer = QuestionnaireModel.withAnswers(request().queryString()).getAnswer();
        return ok(page(disqualified.render(answer)));
    }

    public Result qualified() {
        QuestionnaireModel questions = QuestionnaireModel.withAnswers(request().queryString());
        return ok(page(qualified.render(questions.getAnswer(), questions)));

    }

    public Result answer() {
        QuestionnaireModel model = QuestionnaireModel.withAnswers(request().queryString());
        CalculatorModel calculatorModel = new CalculatorModel(
                getQueryParameter("start-year"),
                getQueryParameter("start-month"),
                getQueryParameter("start-day"),
                getQueryParameter("end-year"),
                getQueryParameter("end-month"),
                getQueryParameter("end-day"));

        return ok(page(answer.render(model,calculatorModel)));
    }
}
