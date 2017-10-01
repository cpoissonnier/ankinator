package infra.adapter;


import com.google.inject.Inject;
import domain.Formatter;
import domain.report.ReportAdapter;
import domain.report.SearchReport;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import static play.mvc.Results.*;

public class AnkiFileAdapter implements ReportAdapter {

    private Formatter formatter;

    @Inject
    public AnkiFileAdapter() {
        this.formatter = new AnkiFormatter();
    }

    public Result generateReport(SearchReport report) {
        String fileContent = report.searchResults.stream()
                                                 .map(searchResult -> formatter.format(searchResult))
                                                 .collect(Collectors.joining("\n"));

        try {
            InputStream inputStream = new ByteArrayInputStream(fileContent.toString().getBytes("UTF-8"));
            Http.Context.current().response().setHeader("Content-disposition", "attachment; filename=export.txt");
            return ok(inputStream).as(("application/x-download"));
        } catch (UnsupportedEncodingException e) {
            Logger.error("Error while building anki file", e);
            return internalServerError(e.getMessage());
        }
    }
}
