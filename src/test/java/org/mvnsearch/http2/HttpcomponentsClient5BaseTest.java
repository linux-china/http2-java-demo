package org.mvnsearch.http2;

import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2RequesterBootstrap;
import org.apache.hc.core5.http2.ssl.H2ClientTlsStrategy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.jetbrains.annotations.NotNull;

/**
 * HttpComponents Client 5 Base test
 *
 * @author linux_china
 */
public interface HttpcomponentsClient5BaseTest {
    @NotNull
    default HttpAsyncRequester getHttp2AsyncRequester() {
        final H2Config h2Config = H2Config.custom()
                .setPushEnabled(false)
                .build();
        final HttpAsyncRequester requester = H2RequesterBootstrap.bootstrap()
                .setH2Config(h2Config)
                .setVersionPolicy(HttpVersionPolicy.NEGOTIATE)
                .setTlsStrategy(new H2ClientTlsStrategy(SSLContexts.createSystemDefault(), (endpoint, sslEngine) -> {
                    // IMPORTANT uncomment the following line when running Java 9 or older
                    // in order to avoid the illegal reflective access operation warning
                    // ====
                    // return new TlsDetails(sslEngine.getSession(), sslEngine.getApplicationProtocol());
                    // ====
                    return null;
                }))
                .create();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> requester.close(CloseMode.GRACEFUL)));
        requester.start();
        return requester;
    }

    @NotNull
    default MinimalHttpAsyncClient getReactiveHttpAsyncClient() {
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5))
                .build();
        final MinimalHttpAsyncClient client = HttpAsyncClients.createMinimal(
                HttpVersionPolicy.NEGOTIATE,
                H2Config.DEFAULT,
                Http1Config.DEFAULT,
                ioReactorConfig);
        client.start();
        return client;
    }
}
