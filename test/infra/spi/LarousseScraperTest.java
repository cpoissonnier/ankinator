package infra.spi;

import domain.SearchStatus;
import domain.WordSearchResult;
import org.junit.Test;
import play.test.WithApplication;

import static org.assertj.core.api.Assertions.assertThat;

public class LarousseScraperTest extends WithApplication {


    @Test
    public void when_get_a_known_word_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("test");
        assertThat(word.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(word.meanings).isNotNull();
        assertThat(word.meanings).isNotEmpty();
        assertThat(word.meanings).hasSize(8);
        assertThat(word.meanings).first().isEqualTo("&Eacute;preuve psychologique impliquant une t&acirc;che &agrave; remplir identique pour tous les sujets, des conditions d'application rigoureuses et une technique pr&eacute;cise pour l'appr&eacute;ciation du succ&egrave;s ou de l'&eacute;chec. (Il existe des tests de niveau et des tests projectifs.)");
        assertThat(word.etymology).isEqualTo("(anglais <i>test, </i>&eacute;preuve, du fran&ccedil;ais t&ecirc;t)");
    }

    @Test
    public void when_get_a_unknow_word_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("azertyuiop");
        assertThat(word.status).isEqualByComparingTo(SearchStatus.NOT_FOUND);
    }

    @Test
    public void when_get_a_known_word_with_accent_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("Clément");
        assertThat(word).isNotNull();
        assertThat(word.meanings).isNotNull();
        assertThat(word.meanings).isNotEmpty();
        assertThat(word.meanings).hasSize(2);
        assertThat(word.etymology).isNotEmpty();
    }

    @Test
    public void when_get_a_known_word_without_etymology_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("chlorhydrique");
        assertThat(word).isNotNull();
        assertThat(word.meanings).isNotNull();
        assertThat(word.meanings).isNotEmpty();
        assertThat(word.etymology).isEmpty();
    }

    @Test
    public void when_get_a_word_with_weird_plural_get_plural_as_request() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("bec-de-corbeau");
        assertThat(word.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(word.meanings).isNotNull();
        assertThat(word.request).isEqualTo("bec-de-corbeau, becs-de-corbeau");
    }

    @Test
    public void when_we_search_a_word_which_is_an_expression_we_get_info_about_the_expression_and_not_the_word() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("Bleu barbeau");
        assertThat(word.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(word.meanings).isNotNull()
                                 .hasSize(1);
        assertThat(word.meanings).first().isEqualTo("bleu clair.");
        assertThat(word.etymology).isNull();
        assertThat(word.gender).isNull();
        assertThat(word.request).isEqualTo("Bleu barbeau");
    }

    @Test
    public void when_we_search_a_word_with_commas_to_give_feminine_way_to_write_a_word_then_the_best_word_info_is_fetched() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult word = scraper.search("Bilieux, euse");
        assertThat(word.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(word.meanings).isNotNull()
                                 .hasSize(1);
        assertThat(word.meanings).first().isEqualTo("Qui est irascible, d'humeur acari&acirc;tre&nbsp;: <span class=\"ExempleDefinition\">Un critique bilieux.</span>");
        assertThat(word.gender).isEqualTo("adjectif et nom");
    }
}
