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
        WordSearchResult work = scraper.search("test");
        assertThat(work.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(work.meanings).isNotNull();
        assertThat(work.meanings).isNotEmpty();
        assertThat(work.meanings).hasSize(8);
        assertThat(work.meanings).first().isEqualTo("Épreuve psychologique impliquant une tâche à remplir identique pour tous les sujets, des conditions d'application rigoureuses et une technique précise pour l'appréciation du succès ou de l'échec. (Il existe des tests de niveau et des tests projectifs.)");
        assertThat(work.etymology).isEqualTo("(anglais test, épreuve, du français têt)");
    }

    @Test
    public void when_get_a_unknow_word_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult work = scraper.search("azertyuiop");
        assertThat(work.status).isEqualByComparingTo(SearchStatus.NOT_FOUND);
    }

    @Test
    public void when_get_a_known_word_with_accent_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult work = scraper.search("Clément");
        assertThat(work).isNotNull();
        assertThat(work.meanings).isNotNull();
        assertThat(work.meanings).isNotEmpty();
        assertThat(work.meanings).hasSize(2);
        assertThat(work.etymology).isNotEmpty();
    }

    @Test
    public void when_get_a_known_word_without_etymology_get_meaning() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult work = scraper.search("chlorhydrique");
        assertThat(work).isNotNull();
        assertThat(work.meanings).isNotNull();
        assertThat(work.meanings).isNotEmpty();
        assertThat(work.etymology).isEmpty();
    }

    @Test
    public void when_get_a_word_with_weird_plural_get_plural_as_request() {
        LarousseScraper scraper = new LarousseScraper();
        WordSearchResult work = scraper.search("bec-de-corbeau");
        assertThat(work.status).isEqualByComparingTo(SearchStatus.SUCCESS);
        assertThat(work.meanings).isNotNull();
        assertThat(work.request).isEqualTo("bec-de-corbeau, becs-de-corbeau");
    }

}
