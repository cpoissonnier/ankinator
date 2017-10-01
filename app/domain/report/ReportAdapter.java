package domain.report;

import play.mvc.Result;

public interface ReportAdapter {
    Result generateReport(SearchReport report);
}
