import com.google.inject.AbstractModule;
import domain.dictionary.Dictionary;
import infra.spi.LarousseScraper;
import play.libs.akka.AkkaGuiceSupport;

public class InjectionModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bind(Dictionary.class).to(LarousseScraper.class).asEagerSingleton();
    }
}
