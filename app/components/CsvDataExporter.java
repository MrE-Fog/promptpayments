package components;

import com.google.inject.Inject;
import models.CompanySummary;
import models.ReportModel;
import play.libs.F;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Creates a CSV string based on a data export
 */
public class CsvDataExporter {

    @Inject
    private ReportsRepository reportsRepository;

    private static String header="Filing date,Company,Company number, Number one, Number two, Number three\n";

    public String GenerateCsv() {
        List<F.Tuple<CompanySummary, ReportModel>> data = reportsRepository.ExportData(24);

        Stream<String> stringStream = data.stream().map(x -> String.join(",",
                escape(x._2.Info.UiDateString()),
                escape(x._1.Name),
                escape(x._1.CompaniesHouseIdentifier),
                escape(x._2.NumberOne),
                escape(x._2.NumberTwo),
                escape(x._2.NumberThree)));

        return header + String.join("\n", stringStream.collect(Collectors.toList()));
    }

    private String escape(Object obj) {
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
