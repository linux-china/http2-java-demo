nghttp:
   nghttp https://localhost:8443/

h2c:
   nghttp http://localhost:8443/

curl:
   curl -v --http2 https://localhost:8443/

uds:
  curl -GET --unix-socket /tmp/test.sock http://localhost/

demo:
  NODE_TLS_REJECT_UNAUTHORIZED=0 bun run demo.ts
