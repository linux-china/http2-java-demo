let text = await fetch('https://localhost:8443/', {
    headers: {
        'user-agent': 'Mozilla/4.0 MDN Example',
        'content-type': 'application/json'
    },
    method: 'GET' // *GET, POST, PUT, DELETE, etc.
})
    .then(function (response) {
       return response.text();
    });

console.log(text);