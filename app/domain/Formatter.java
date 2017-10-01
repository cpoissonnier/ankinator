package domain;

import domain.report.WordSearchResult;

public interface Formatter {
    String format(WordSearchResult searchResult);
}
