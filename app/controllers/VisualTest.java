package controllers;

import com.google.inject.Inject;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;
import play.twirl.api.HtmlFormat;
import scala.collection.JavaConversions;
import utils.MockUtcTimeProvider;
import views.html.Home.index;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class VisualTest extends PageController {

    private Form<ReportFilingModel> reportForm;

    @Inject
    public VisualTest(FormFactory formFactory) {
        reportForm = formFactory.form(ReportFilingModel.class);
    }

    public Result index() {

        Calendar time = new MockUtcTimeProvider(2016,9,1).Now();
        ReportSummary healthyReportSummary = new ReportSummary(1, time);

        ReportModel healthyReportModel = new ReportModel(healthyReportSummary);
        healthyReportModel.NumberOne = new BigDecimal("1.00");
        healthyReportModel.NumberTwo = new BigDecimal("2.00");
        healthyReportModel.NumberThree= new BigDecimal("3.00");

        ReportModel emptyReportModel = new ReportModel(healthyReportSummary);
        CompanySummary healthyCompanySummary = new CompanySummary("Eigencode Ltd.", "123");

        CompanyModel healthyCompanyModel = new CompanyModel(healthyCompanySummary, Arrays.asList(healthyReportSummary, healthyReportSummary, healthyReportSummary));

        ReportFilingModel newReportFilingModel = new ReportFilingModel();
        newReportFilingModel.setTargetCompanyCompaniesHouseIdentifier("123");

        ReportFilingModel completeReportFilingModel = new ReportFilingModel();
        completeReportFilingModel.setTargetCompanyCompaniesHouseIdentifier("123");
        completeReportFilingModel.setNumberOne(1);
        completeReportFilingModel.setNumberTwo(3);
        completeReportFilingModel.setNumberThree(2);

        Html html = HtmlFormat.fill(JavaConversions.asScalaBuffer(Arrays.asList(

                views.html.Home.index.render(),
                views.html.Home.about.render(),
                views.html.Home.accessData.render(),

                views.html.Reports.report.render(healthyReportModel, healthyCompanySummary),
                views.html.Reports.report.render(emptyReportModel, healthyCompanySummary),

                views.html.Reports.searchstart.render(),
                views.html.Reports.search.render(),
                views.html.Reports.results.render("cod", Arrays.asList(healthyCompanySummary, healthyCompanySummary, healthyCompanySummary)),
                views.html.Reports.company.render(healthyCompanyModel),

                views.html.FileReport.index.render(),
                views.html.FileReport.page1.render(),
                views.html.FileReport.page2.render(Arrays.asList(healthyCompanySummary, healthyCompanySummary, healthyCompanySummary)),
                views.html.FileReport.file.render(reportForm.fill(newReportFilingModel), healthyCompanySummary, new UiDate(time)),
                views.html.FileReport.review.render(reportForm.fill(completeReportFilingModel), healthyCompanySummary, new UiDate(time))

        )).toList());

        return ok(page(html));
    }
}


