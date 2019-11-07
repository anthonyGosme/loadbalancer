const http = require('http');
const port = 9090;
var TimeFootPrint = Date.now() % 1000;

const requestHandler = (request, response) => {
    console.log(request.url);
	if ((request.method === 'POST') || (request.method === 'PUT')) {
    let body = '';
    request.on('data', chunk => {
        body += chunk.toString();
    });
    request.on('end', () => {
        console.log(body);
		response.end(body);
       });
	}
	else
	{
		var reponseMesage = 'server time footprint:' + TimeFootPrint + '\n';
		reponseMesage += request.method + "\n";
		reponseMesage += request.url + "\n";
		reponseMesage += JSON.stringify(request.headers);
		response.end(reponseMesage);
		console.log(reponseMesage);
    }
};

const server = http.createServer(requestHandler);
server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err);
    }
    console.log(`server is listening on ${port}\nserver time footprint: ` + TimeFootPrint);
})