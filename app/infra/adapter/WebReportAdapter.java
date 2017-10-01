package infra.adapter;

import domain.report.ReportAdapter;
import domain.report.SearchReport;
import play.mvc.Result;

import static play.mvc.Results.ok;

public class WebReportAdapter implements ReportAdapter {

    public WebReportAdapter() {
    }


    @Override
    public Result generateReport(SearchReport report) {
        return ok(views.html.result.render(report));
    }
}
