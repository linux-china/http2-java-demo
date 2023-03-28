package org.mvnsearch.http2;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Notification;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactive.ReactiveEntityProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * httpcomponents client 5 Reactive test
 *
 * @author linux_china
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpComponentsReactiveClientTest implements HttpcomponentsClient5BaseTest {
    private CloseableHttpAsyncClient httpAsyncClient;

    @BeforeAll
    public void setUp() {
        httpAsyncClient = getReactiveHttpAsyncClient();
    }

    @AfterAll
    public void tearDown() {
        System.out.println("Shutting down");
        httpAsyncClient.close(CloseMode.GRACEFUL);
    }

    @Test
    public void testReactiveOperator() throws Exception {
        final URI requestUri = new URI("http://httpbin.org/post");
        final byte[] bs = "stuff".getBytes(StandardCharsets.UTF_8);
        final ReactiveEntityProducer reactiveEntityProducer = new ReactiveEntityProducer(
                Flowable.just(ByteBuffer.wrap(bs)), bs.length, ContentType.TEXT_PLAIN, null);
        final BasicRequestProducer requestProducer = new BasicRequestProducer(
                "POST", requestUri, reactiveEntityProducer);

        final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();
        final Future<Void> requestFuture = httpAsyncClient.execute(requestProducer, consumer, null);
        final Message<HttpResponse, Publisher<ByteBuffer>> streamingResponse = consumer.getResponseFuture().get();

        System.out.println(streamingResponse.getHead());
        for (final Header header : streamingResponse.getHead().getHeaders()) {
            System.out.println(header.toString());
        }
        System.out.println();

        Observable.fromPublisher(streamingResponse.getBody())
                .map(new Function<ByteBuffer, String>() {
                    @Override
                    public String apply(final ByteBuffer byteBuffer) throws Exception {
                        final byte[] string = new byte[byteBuffer.remaining()];
                        byteBuffer.get(string);
                        return new String(string);
                    }
                })
                .materialize()
                .forEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(final Notification<String> byteBufferNotification) throws Exception {
                        System.out.println(byteBufferNotification.toString());
                    }
                });
        requestFuture.get(1, TimeUnit.MINUTES);
    }

}
