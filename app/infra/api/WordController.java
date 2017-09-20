package infra.api;

import domain.Dictionary;
import domain.SearchReport;
import domain.SearchStatus;
import domain.WordSearchResult;
import infra.spi.LarousseScraper;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class WordController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    Dictionary dictionary;

    public Result get(String word) {
        WordSearchResult wordInfo = dictionary.search(word);

        return ok(wordInfo.exportAsHtml());
    }

    public Result fetchInfos() throws UnsupportedEncodingException {
        // FIXME : corriger l'encoding pour gérer le cas relou du Æa à la place du '
        String words = formFactory.form().bindFromRequest().get("words").replaceAll("Æa", "'");
        String[] splittedWords = words.split("\n");

        Logger.info("Fetching info for {} words : {}", splittedWords.length, words);

        SearchReport report = new SearchReport();
        report.searchResults = Arrays.asList(splittedWords)
                                     .stream()
                                     .map(word -> word.replaceAll("\r", ""))
                                     .map(word -> dictionary.search(word))
                                     .collect(Collectors.toList());

        String fileContent = report.searchResults.stream()
                                                 .map(searchResult -> searchResult.exportAsAnkiLine())
                                                 .collect(Collectors.joining("\n"));

        InputStream inputStream = new ByteArrayInputStream(fileContent.toString().getBytes("UTF-8"));

        List<WordSearchResult> errors = report.searchResults.stream()
                                                            .filter(wordSearchResult -> wordSearchResult.status != SearchStatus.SUCCESS)
                                                            .collect(Collectors.toList());

        if (errors != null && !errors.isEmpty()) {
            String requestsInError = errors.stream()
                                           .map(searchResult -> searchResult.request)
                                           .collect(Collectors.joining(", "));

            Logger.info("Les mots suivants n'ont pas été inclus dans le résultat (erreur ou mot inexistant) : {}", requestsInError);
            flash("errors", requestsInError);
        }

//        return ok(views.html.result.render(report));
//
        response().setHeader("Content-disposition", "attachment; filename=export.txt");
        return ok(inputStream).as(("application/x-download"));
    }
}
