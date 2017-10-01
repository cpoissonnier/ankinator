package domain.dictionary;

import domain.report.WordSearchResult;

public interface Dictionary {
    WordSearchResult search(String word);
}
