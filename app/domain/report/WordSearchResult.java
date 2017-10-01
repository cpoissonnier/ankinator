package domain.report;

import java.util.ArrayList;
import java.util.List;

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
}
