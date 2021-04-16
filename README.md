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


### Reference

* HTTP 2.0 specification: https://http2.github.io
* mkcert: https://github.com/FiloSottile/mkcert
* HttpCore 5.0 Examples: https://hc.apache.org/httpcomponents-core-5.0.x/examples.html
* HTTP/2 in Netty: https://www.baeldung.com/netty-http2
