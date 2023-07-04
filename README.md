HTTP 2.0 Server Demo
=========================

Spring Boot App with HTTP/2 enabled

### HTTP Client

* OkHttp3 4.4.0: https://square.github.io/okhttp/
* httpcomponents-client-5: https://hc.apache.org/httpcomponents-client-5.0.x/index.html

### User mkcert to produce pkcs12 keystore file

```
mkcert -pkcs12 localhost
```

### HTTP/2 over TCP (h2c)

All four embedded web containers now support HTTP/2 over TCP (h2c) without any manual customization.
To enable h2c, set `server.http2.enabled` is true and leave `server.ssl.enabled` set to false (its default value).

Caddy Server reverse_proxy configuration:

```
reverse_proxy {
    to http://127.0.0.1:2012
    transport http {
        versions h2c
    }
}
```

### UDS support - Unix Domain Sockets

* Add dependency

```xml
       <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-transport-native-kqueue</artifactId>
            <version>${netty.version}</version>
            <classifier>osx-x86_64</classifier>
        </dependency>
```

* Create NettyUdsConfig.java

```java
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
```
* Start server and test

```
curl -GET --unix-socket /tmp/test.sock http://localhost/
```

### Reference

* Spring Boot SSL: https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/features.html#features.ssl
* HTTP 2.0 specification: https://http2.github.io
* mkcert: https://github.com/FiloSottile/mkcert
* HttpCore 5.0 Examples: https://hc.apache.org/httpcomponents-core-5.0.x/examples.html
* HTTP/2 in Netty: https://www.baeldung.com/netty-http2
* How I can tell alias of the wanted key-entry to SSLSocket before connecting? https://stackoverflow.com/questions/15201251/how-i-can-tell-alias-of-the-wanted-key-entry-to-sslsocket-before-connecting
* tls demo:  https://github.com/linux-china/tls-demo
* Spring SSL: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.webserver.configure-ssl
* Three Ways to Run Your Java Locally with HTTPS: https://developer.okta.com/blog/2022/01/31/local-https-java
