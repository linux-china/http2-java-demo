package org.mvnsearch.http2;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * httpcomponents client5 test
 *
 * @author linux_china
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpcomponentsClient5Test implements HttpcomponentsClient5BaseTest {
    private HttpAsyncRequester requester;

    @BeforeAll
    public void setUp() {
        requester = getHttp2AsyncRequester();
    }

    @AfterAll
    public void tearDown() {
        requester.initiateShutdown();
        System.out.println("Shutting down I/O reactor");
    }

    @Test
    public void testHttp2Get() throws Exception {
        // Create and start requester
        final HttpHost target = new HttpHost("https", "localhost", 8443);
        final String[] requestUris = new String[]{"/"};
        final CountDownLatch latch = new CountDownLatch(requestUris.length);
        for (final String requestUri : requestUris) {
            final Future<AsyncClientEndpoint> future = requester.connect(target, Timeout.ofSeconds(5));
            final AsyncClientEndpoint clientEndpoint = future.get();
            clientEndpoint.execute(
                    new BasicRequestProducer(Method.GET, target, requestUri),
                    new BasicResponseConsumer<>(new StringAsyncEntityConsumer()),
                    new FutureCallback<Message<HttpResponse, String>>() {

                        @Override
                        public void completed(final Message<HttpResponse, String> message) {
                            clientEndpoint.releaseAndReuse();
                            final HttpResponse response = message.getHead();
                            final String body = message.getBody();
                            System.out.println(requestUri + "->" + response.getCode() + " " + response.getVersion());
                            System.out.println(body);
                            latch.countDown();
                        }

                        @Override
                        public void failed(final Exception ex) {
                            clientEndpoint.releaseAndDiscard();
                            System.out.println(requestUri + "->" + ex);
                            latch.countDown();
                        }

                        @Override
                        public void cancelled() {
                            clientEndpoint.releaseAndDiscard();
                            System.out.println(requestUri + " cancelled");
                            latch.countDown();
                        }

                    });
        }
        latch.await();
    }

}
