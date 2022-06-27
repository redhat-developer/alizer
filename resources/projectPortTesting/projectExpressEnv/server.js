const express = require('express');
const http = require('http');

const app = express();
const server = http.createServer(app)

app.get('/', (req, res) => {	
  // Use req.log (a `pino` instance) to log JSON:	
  req.log.info({message: 'Hello from my Node.js App!'});		
  res.send('Hello from my Node.js App!');	
});	

app.get('*', (req, res) => {
  res.status(404).send("Not Found");
});

// Listen and serve.
server.listen(process.env.TEST_EXPRESS_ENV, () => {
  console.log(`App started on PORT ${process.env.TEST_EXPRESS_ENV}`);
});
