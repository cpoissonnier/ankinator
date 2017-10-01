package infra.spi;


import domain.Dictionary;
import domain.SearchStatus;
import domain.WordSearchResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


public class LarousseScraper implements Dictionary {

    private static String ENDPOINT = "http://larousse.fr/dictionnaires/francais/";

    public WordSearchResult search(String word) {
        WordSearchResult searchResult = new WordSearchResult();
        searchResult.request = word;

        boolean wordHasBeenFound;
        try {


            URL url = new URL(ENDPOINT + formatWordForRequest(word));
            Document doc = Jsoup.connect(url.toString()).get();

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

        if (wordIsAnExpression(doc)) {
            return originalWord;
        }

        // On remplace le non breaking whitespace en espace classique pour pouvoir trimmer
        String trimmedLarousseWord = doc.select(".AdresseDefinition").text().replace('\u00A0', ' ').trim();

        // On souhaite récupérer les infos rajoutées par Larousse
        // Exemple : le pluriel dans le cas où il n'est pas évident, car il est fourni par Larousse.
        // Par contre, parfois le terme cherché est réduit par Larousse (exemple : bleu barbeau), et dans ce cas là, on veut conserver le terme de recherche
        if(trimmedLarousseWord.length() > originalWord.length()) {
            return trimmedLarousseWord;
        } else {
            return originalWord;
        }
    }

    private Object formatWordForRequest(String word) throws UnsupportedEncodingException {
        String firstTerm;
        if (word.indexOf(",") != -1) {
            firstTerm = word.substring(0, word.indexOf(","));
        } else {
            firstTerm = word;
        }
        return StringUtils.stripAccents(firstTerm.toLowerCase().replaceAll("’", "'").replaceAll("'", "_").replaceAll(" ", "%20"));
    }

    private String extractEtymology(Document doc) {
        if (wordIsAnExpression(doc)) {
            return null;
        } else {
            return doc.select(".OrigineDefinition").text();
        }
    }
    private String extractGender(Document doc) {
        if (wordIsAnExpression(doc)) {
            return null;
        } else {
            return doc.select(".CatgramDefinition").text();
        }
    }

    private List<String> extractMeanings(Document doc) {
        if (wordIsAnExpression(doc)) {
            // Récupération de l'identifiant de la locution, qui permet de récupérer la définition associée
            String locutionId = doc.baseUri().substring(doc.baseUri().lastIndexOf("#") + 1);
            return doc.select("#" + locutionId + ">.TexteLocution").stream()
                      .map(element -> element.text())
                      .collect(Collectors.toList());
        } else {
            return doc.select(".DivisionDefinition").stream()
                      .map(element -> element.text())
                      .collect(Collectors.toList());
        }
    }

    private boolean wordIsAnExpression(Document doc) {
        // le mot recherché est une expression si le second onglet (Expression) est sélectionné
        return !doc.select("li.sel:nth-child(2)").isEmpty();
    }


    private boolean wordHasBeenFound(Document doc) {
        return !doc.select(".AdresseDefinition").isEmpty();
    }
}
