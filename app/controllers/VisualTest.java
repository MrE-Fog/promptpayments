package controllers;

import com.google.inject.Inject;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;
import play.twirl.api.HtmlFormat;
import scala.collection.JavaConversions;
import utils.DatePickerHelper;
import utils.MockUtcTimeProvider;
import components.PagedList;
import utils.TimeProvider;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class VisualTest extends PageController {

    private Form<ReportFilingModel> reportForm;
    private TimeProvider timeProvider;

    @Inject
    public VisualTest(FormFactory formFactory, TimeProvider timeProvider) {
        reportForm = formFactory.form(ReportFilingModel.class);
        this.timeProvider = timeProvider;
    }


    public Result index() {

        Calendar time = new MockUtcTimeProvider(2016, 9, 1).Now();
        ReportSummary healthyReportSummary = new ReportSummary(1, time, time, time);

        ReportModel healthyReportModel = new ReportModel(
                healthyReportSummary,
                new BigDecimal("31.00"),
                new BigDecimal("10.00"),
                new BigDecimal("80.00"),
                new BigDecimal("15.00"),
                new BigDecimal("5.00"),

                new MockUtcTimeProvider(2016, 0, 0).Now(),
                new MockUtcTimeProvider(2016, 5, 0).Now(),

                "User-supplied description of payment terms",
                "Max contract period",
                true,
                "description of change",
                true,
                "description of notification",
                "Payment terms comments",
                "User-supplied description of dispute resolution",

                true,
                true,
                false,
                false,
                true,
                "Prompt Payment Code"
        );

        ReportModel emptyReportModel = new ReportModel(healthyReportSummary, null, null, null, null, null, new MockUtcTimeProvider(2016, 0, 1).Now(), new MockUtcTimeProvider(2016, 5, 30).Now(), null, null, false, null, false, null, null, null, false, false, false, false, false, null);
        CompanySearchResult healthyCompanySearchResult = new CompanySearchResult(
                new CompanySummaryWithAddress("Eigencode Ltd.", "123", "1 Stott Gardens"),
                3);

        CompanyModel healthyCompanyModel = new CompanyModel(healthyCompanySearchResult, new PagedList<>(Arrays.asList(healthyReportSummary, healthyReportSummary, healthyReportSummary), 6, 0, 3));

        ReportFilingModel newReportFilingModel = ReportFilingModel.MakeEmptyModelForTarget("123");

        ReportFilingModel completeReportFilingModel = getCompleteFilingModel();
        ReportFilingModel faultyReportFilingModel = getFaultyFilingModel();
        ReportFilingModel dateswappedFilingModel = getDateswappedFilingModel();


        Html html = HtmlFormat.fill(JavaConversions.asScalaBuffer(Arrays.asList(
                views.html.Home.index.render(),
                views.html.Home.ifGuide.render(),
                views.html.Home.howGuide.render(0, false, new CalculatorModel()),
                views.html.Home.howGuide.render(1, false, new CalculatorModel("2018", "1", "1", "2018", "12", "31")),
                views.html.Home.howGuide.render(2, false, new CalculatorModel()),
                views.html.Home.accessData.render(),

                views.html.Reports.report.render(healthyReportModel, healthyCompanySearchResult),
                views.html.Reports.report.render(emptyReportModel, healthyCompanySearchResult),

                views.html.Reports.results.render("cod", new PagedList<>(Arrays.asList(healthyCompanySearchResult, healthyCompanySearchResult, healthyCompanySearchResult), 100, 0, 3)),
                views.html.Reports.company.render(healthyCompanyModel),

                views.html.FileReport.start.render(new CompanySummary("EIGENCODE LTD", "12345678")),
                views.html.FileReport.signInInterstitial.render("123"),
                views.html.FileReport.companiesHouseAccount.render(),

                views.html.FileReport.file.render(reportForm.fill(newReportFilingModel), new AllOkReportFilingModelValidation(), healthyCompanySearchResult, new UiDate(time), new DatePickerHelper(timeProvider)),
                views.html.FileReport.file.render(reportForm.fill(completeReportFilingModel), new AllOkReportFilingModelValidation(), healthyCompanySearchResult, new UiDate(time), new DatePickerHelper(timeProvider)),
                views.html.FileReport.file.render(reportForm.fill(faultyReportFilingModel), new ReportFilingModelValidationImpl(faultyReportFilingModel, time), healthyCompanySearchResult, new UiDate(time), new DatePickerHelper(timeProvider)),
                views.html.FileReport.file.render(reportForm.fill(dateswappedFilingModel), new ReportFilingModelValidationImpl(dateswappedFilingModel, time), healthyCompanySearchResult, new UiDate(time), new DatePickerHelper(timeProvider)),
                views.html.FileReport.review.render(reportForm.fill(completeReportFilingModel), healthyCompanySearchResult, new UiDate(time), true),

                views.html.FileReport.filingSuccess.render("123", 1, "foobar@example.com")
        )).toList());

        return ok(page(html));
    }

    private ReportFilingModel getCompleteFilingModel() {
        return ReportFilingModel.makeReportFilingModel(
                "122",
                31,
                10,
                80,
                15,
                5,

                "2016",
                "1",
                "1",

                "2016",
                "6",
                "29",

                "Payment terms",
                "max contract period",
                true,
                "description of changes",
                true,
                "description of notification",
                "comments on payment terms",
                "Dispute resolution",
                true,
                "Payment codes",

                true,
                true,
                false,
                false);
    }

    private ReportFilingModel getFaultyFilingModel() {
        return ReportFilingModel.makeReportFilingModel(
                "122",
                -100,
                200,
                50,
                50,
                50,

                "2020",
                "1",
                "1",

                "2019",
                "2",
                "31",

                "",
                "",
                null,
                "",
                null,
                "",
                "",
                "",
                null,
                "",

                null,
                null,
                null,
                null
        );
    }

    private ReportFilingModel getDateswappedFilingModel() {
        ReportFilingModel completeFilingModel = getCompleteFilingModel();
        String year = completeFilingModel.getStartDate_year();
        String month = completeFilingModel.getStartDate_month();
        String day = completeFilingModel.getStartDate_day();

        completeFilingModel.setStartDate_year(completeFilingModel.getEndDate_year());
        completeFilingModel.setStartDate_month(completeFilingModel.getEndDate_month());
        completeFilingModel.setStartDate_day(completeFilingModel.getEndDate_day());

        completeFilingModel.setEndDate_year(year);
        completeFilingModel.setEndDate_month(month);
        completeFilingModel.setEndDate_day(day);
        return completeFilingModel;
    }
}


