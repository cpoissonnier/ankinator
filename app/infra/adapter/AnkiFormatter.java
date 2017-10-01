package infra.adapter;


import domain.Formatter;
import domain.report.WordSearchResult;

import java.util.stream.Collectors;

public class AnkiFormatter implements Formatter {

    @Override
    public String format(WordSearchResult searchResult) {
        return searchResult.request + "\t" + formatMeaningsAsHtml(searchResult) + "\t";
    }

    private String formatMeaningsAsHtml(WordSearchResult searchResult) {
        String meaningList = searchResult.meanings != null ? "<ul>" + searchResult.meanings.stream().map(meaning -> "<li>" + meaning + "</li>").collect(Collectors.joining("")) + "</ul>" : "";
        return "<p><i>" + searchResult.gender + "</i></p>" + "<p><i>" + searchResult.etymology + "</i></p>" + meaningList;
    }
}
