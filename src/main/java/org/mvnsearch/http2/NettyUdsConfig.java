package org.mvnsearch.http2;

import io.netty.channel.unix.DomainSocketAddress;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Netty UDS configuration
 */
@Configuration
public class NettyUdsConfig {

    @Bean
    public NettyReactiveWebServerFactory factory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.setServerCustomizers(Collections.singletonList(httpServer -> httpServer.bindAddress(() -> new DomainSocketAddress("/tmp/test.sock"))));
        return factory;
    }
}
