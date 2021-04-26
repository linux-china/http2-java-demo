nghttp:
   nghttp https://localhost:8443/

h2c:
   nghttp http://localhost:8443/

testing domain:
   curl -X GET --location "https://{{domain}}:8443" --resolve '{{domain}}:8443:127.0.0.1'