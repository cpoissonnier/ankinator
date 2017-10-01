package infra.spi;


import domain.Dictionary;
import domain.SearchStatus;
import domain.WordSearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

import java.io.IOException;
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

            wordHasBeenFound = wordHasBeenFound(doc);
            if (wordHasBeenFound) {
                searchResult.request = extractWord(doc, word);
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

    private String extractWord(Document doc, String originalWord) {
        String larousseWord = doc.select(".AdresseDefinition").text();
        // On remplace le non breaking whitespace en espace classique pour pouvoir trimmer
        String trimmedLarousseWord = larousseWord.replace('\u00A0', ' ').trim();
        // On souhaite récupérer les infos rajoutées par Larousse
        // Exemple : le pluriel dans le cas où il n'est pas évident, car il est fourni par Larousse.
        // Par contre, parfois le terme cherché est réduit par Larousse (exemple : bleu barbeau), et dans ce cas là, on veut conserver le terme de recherche
        if(trimmedLarousseWord.length() > originalWord.length()) {
            return trimmedLarousseWord;
        } else {
            return originalWord;
        }
    }

    private Object formatWordForRequest(String word) {
        return StringEscapeUtils.escapeHtml4(StringUtils.stripAccents(word.toLowerCase().replaceAll("’", "'").replaceAll("'", "_")));
    }

    private String extractEtymology(Document doc) {
        return doc.select(".OrigineDefinition").text();
    }
    private String extractGender(Document doc) {
        return doc.select(".CatgramDefinition").text();
    }

    private List<String> extractMeanings(Document doc) {
        return doc.select(".DivisionDefinition").stream()
                  .map(element -> element.text())
                  .collect(Collectors.toList());
    }

    private boolean wordHasBeenFound(Document doc) {
        return !doc.select(".AdresseDefinition").isEmpty();
    }
}
