package components;

import com.google.inject.Inject;
import models.CompanySummary;
import models.ReportModel;
import models.UiDate;
import play.libs.F;
import utils.TimeProvider;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Creates a CSV string based on a data export
 */
public class CsvDataExporter {

    private final TimeProvider timeProvider;
    private final ReportsRepository reportsRepository;

    @SuppressWarnings("FieldCanBeLocal")
    private static String header="Start date,End date,Filing date,Company,Company number,Average time to pay,% Invoices paid within 30 days,% Invoices paid within 60 days,% Invoices paid later than 60 days,E-Invoicing offered,Supply-chain financing offered,Policy covers charges for remaining on supplier list,Charges have been made for remaining on supplier list,Payment terms,Dispute resolution facilities,Payment codes membership\n";

    private static final int cacheMinutes = 30;

    private String cachedCsv;
    private Calendar lastCached;

    @Inject
    CsvDataExporter(ReportsRepository reportsRepository, TimeProvider timeProvider) {
        this.reportsRepository = reportsRepository;
        this.timeProvider = timeProvider;
        cachedCsv = null;
        lastCached = null;
    }

    public String GenerateCsv() {
        if (lastCached != null && cachedCsv != null && timeProvider.Now().getTimeInMillis() - lastCached.getTimeInMillis() < cacheMinutes * 60000) {
            return cachedCsv;
        }

        List<F.Tuple<CompanySummary, ReportModel>> data = reportsRepository.ExportData(24);

        Stream<String> stringStream = data.stream().map(x -> String.join(",",
                escape(new UiDate(x._2.StartDate).ToFriendlyString()),
                escape(new UiDate(x._2.EndDate).ToFriendlyString()),
                escape(x._2.Info.UiDateString()),
                escape(x._1.Name),
                escape(x._1.CompaniesHouseIdentifier),
                escape(x._2.AverageTimeToPay),
                escape(x._2.PercentInvoicesPaidBeyondAgreedTerms),
                escape(x._2.PercentInvoicesWithin30Days),
                escape(x._2.PercentInvoicesWithin60Days),
                escape(x._2.PercentInvoicesBeyond60Days),

                escape(x._2.OfferEInvoicing),
                escape(x._2.OfferSupplyChainFinance),
                escape(x._2.RetentionChargesInPolicy),
                escape(x._2.RetentionChargesInPast),

                escape(x._2.PaymentTerms),
                escape(x._2.DisputeResolution),
                escape(x._2.PaymentCodes)
        ));

        lastCached = timeProvider.Now();
        cachedCsv = header + String.join("\n", stringStream.collect(Collectors.toList()));

        return cachedCsv;
    }

    private String escape(Object obj) {
        if (obj == null) {
            return "";
        }
        String raw = obj.toString();
        if (raw.contains(",")) {
            if (raw.contains("\"")) {
                raw = raw.replace("\"", "\"\"");
            }
            raw = "\"" + raw + "\"";
        }
        return raw;

    }
}
