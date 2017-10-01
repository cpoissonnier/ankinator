package infra.api;

import domain.dictionary.Dictionary;
import domain.report.ReportAdapter;
import domain.report.SearchReport;
import domain.report.SearchStatus;
import domain.report.WordSearchResult;
import infra.adapter.AnkiFormatter;
import infra.adapter.AnkiFileAdapter;
import infra.adapter.WebReportAdapter;
import infra.forms.ReportForm;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    Dictionary dictionary;

    @Inject
    AnkiFormatter ankiFormatter;

    @Inject
    AnkiFileAdapter ankiFileAdapter;

    @Inject
    WebReportAdapter webReportAdapter;

    public Result generateReport() {
        Form<ReportForm> form = formFactory.form(ReportForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest();
        }

        ReportForm reportForm = form.get();
        if ("anki".equals(reportForm.getOutputType())) {
            return generateAnkiReport();
        } else if ("html".equals(reportForm.getOutputType())) {
            return generateHtmlReport();
        } else {
            return badRequest();
        }
    }

    public Result get(String word) {
        WordSearchResult wordInfo = dictionary.search(word);
        return ok(ankiFormatter.format(wordInfo));
    }

    public Result generateAnkiReport() {
        return generateGenericReport(ankiFileAdapter);
    }

    public Result generateHtmlReport() {
        return generateGenericReport(webReportAdapter);
    }

    private Result generateGenericReport(ReportAdapter adapter) {
        List<String> words = getWordsFromRequest();
        SearchReport report = generateSearchReport(words, dictionary);
        handleErrors(report);

        return adapter.generateReport(report);
    }



    private List<String> getWordsFromRequest() {
        // FIXME : corriger l'encoding pour gérer le cas relou du Æa à la place du '
        String words = formFactory.form().bindFromRequest().get("words").replaceAll("Æa", "'").replaceAll("\r", "");
        return Arrays.asList(words.split("\n"));

    }

    private SearchReport generateSearchReport(List<String> words, Dictionary dictionary) {
        SearchReport report = new SearchReport();
        report.searchResults = words.parallelStream()
                                    .map(word -> dictionary.search(word))
                                    .collect(Collectors.toList());
        return report;
    }

    private void handleErrors(SearchReport searchReport) {
        List<WordSearchResult> errors = searchReport.searchResults.stream()
                                                            .filter(wordSearchResult -> wordSearchResult.status != SearchStatus.SUCCESS)
                                                            .collect(Collectors.toList());

        if (errors != null && !errors.isEmpty()) {
            String requestsInError = errors.stream()
                                           .map(searchResult -> searchResult.request)
                                           .collect(Collectors.joining(", "));

            Logger.info("Les mots suivants n'ont pas été inclus dans le résultat (erreur ou mot inexistant) : {}", requestsInError);
            flash("errors", requestsInError);
        }
    }


}
