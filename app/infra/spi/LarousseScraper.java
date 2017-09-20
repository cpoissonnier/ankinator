package infra.spi;


import domain.Dictionary;
import domain.SearchStatus;
import domain.WordSearchResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Collectors;


public class LarousseScraper implements Dictionary {

    private static String ENDPOINT = "http://larousse.fr/dictionnaires/francais/";

    public WordSearchResult search(String word) {
        WordSearchResult searchResult = new WordSearchResult();
        searchResult.request = word;

        boolean wordHasBeenFound;
        try {
            Document doc = Jsoup.connect(ENDPOINT + formatWordForRequest(word)).get();

            wordHasBeenFound = wordFound(doc);
            if (wordHasBeenFound) {
                searchResult.etymology = extractEtymology(doc);
                searchResult.meanings = extractMeanings(doc);
                searchResult.gender = extractGender(doc);
                searchResult.status = SearchStatus.SUCCESS;
            } else {
                searchResult.status = SearchStatus.NOT_FOUND;
            }
        } catch (IOException e) {
            Logger.error("Error while searching " + word + "", e);
            searchResult.status = SearchStatus.ERROR;
        }

        return searchResult;
    }

    private Object formatWordForRequest(String word) {
        return StringUtils.stripAccents(word).toLowerCase().replaceAll("’", "'").replaceAll("'", "_");
    }

    private String extractEtymology(Document doc) {
        return doc.select(".OrigineDefinition").html().replaceAll("\n", "").replaceAll("\r", "");
    }
    private String extractGender(Document doc) {
        return doc.select(".CatgramDefinition").text().replaceAll("\n", "").replaceAll("\r", "");
    }

    private List<String> extractMeanings(Document doc) {
        return doc.select(".DivisionDefinition").stream()
                  .map(element -> element.html().replaceAll("\n", "").replaceAll("\r", ""))
                  .collect(Collectors.toList());
    }

    private boolean wordFound(Document doc) {
        return !doc.select(".AdresseDefinition").isEmpty();
    }

}
