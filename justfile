nghttp:
   nghttp https://localhost:8443/

h2c:
   nghttp http://localhost:8443/

curl:
   curl -v --http2 http://localhost:8443/
 
uds:
  curl -GET --unix-socket /tmp/test.sock http://localhost/
