package org.tensin.sonos.commander;

import org.tensin.sonos.ISonosIndexer;
import org.tensin.sonos.SonosIndexer;
import org.tensin.sonos.commands.IZoneCommandDispatcher;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.web.ISonosState;
import org.tensin.sonos.web.SonosState;

import com.google.inject.AbstractModule;

public class GuiceSonosModuleWEB extends AbstractModule {

    /**
     * Instantiates a new guice log player module.
     */
    public GuiceSonosModuleWEB() {
    }

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // === Class bindings : will be constructed by Guice and injected everywhere an @Inject annotation is put (Guice will inject the same instance / singletion pattern)

        bind(IZoneCommandDispatcher.class).to(ZoneCommandDispatcher.class);
        bind(ISonosController.class).to(WebController.class);
        bind(ISonosState.class).to(SonosState.class);

        final ISonosIndexer sonosIndexer = new SonosIndexer();
        sonosIndexer.init();
        bind(ISonosIndexer.class).toInstance(sonosIndexer);

        // === Instance bindings : guice will inject the provided instance everywhere an @Inject annotation is found for that class
        // if (configuration != null) {
        // bind(IConfiguration.class).toInstance(configuration); // default behavior
        // } else {
        // bind(IConfiguration.class).to(Configuration.class);
        // }

        // === Static injection
        // requestStaticInjection(Service.class);
    }
}
