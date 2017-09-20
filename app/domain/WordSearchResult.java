package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WordSearchResult {

    public String request;

    public List<String> meanings = new ArrayList<>();
    public String etymology;
    public String gender;
    public SearchStatus status;

    @Override
    public String toString() {
        return "WordInfo{" +
                "request='" + request + '\'' +
                ", meanings=" + meanings +
                ", gender=" + gender +
                ", etymology='" + etymology + '\'' +
                '}';
    }

    public String exportAsAnkiLine() {
        return request + "\t" + exportAsHtml() + "\t";
    }

    public String exportAsHtml() {
        String meaningList = meanings != null ? "<ul>" + meanings.stream().map(meaning -> "<li>" + meaning + "</li>").collect(Collectors.joining("")) + "</ul>" : "";
        return "<i>" + etymology + "</i>" + meaningList;
    }
}
