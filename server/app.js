var express = require('express');
var app = express();    

app.enable('trust proxy');
app.get('/', function(req, res) {
    res.send('Server is running');
});

var server = app.listen(3000);

var address = server.address().ip;
var port = server.address().port;
console.log('Server listening at http://%s:%s', address, port);
