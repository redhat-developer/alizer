
const express = require("express");
const app = express(); 
const path = require('path');
const os = require('os');
let cnt = 0;

// static page serving
app.use(express.static(path.join(__dirname, 'html')));

var TEST_EXPRESS_ENV = process.env.TEST_EXPRESS_ENV || 8080;

app.get("/", async(req,res,next) => {
  res.render('index');
})

app.get("/hello", async(req, res) => {
	try {
		res.end(`Node Hello on ${os.hostname()}:${PORT} - ${cnt++} \n`);
	} catch (err) {
		console.error(err.message);
	}  
})

app.get("/environment", async(req, res) => {
	try {
		var env = process.env;
		var results = "ENV\n";
		Object.keys(env).forEach(function(key) {
			results = results + key + ":" + env[key] + "\n";
			console.log(key + ":" + env[key]);
		});
		res.end(results);	
		
	} catch (err) {
		console.error(err.message);
	}  
})

app.listen(TEST_EXPRESS_ENV, function () {
  console.log('my Node app listening on PORT ' + PORT + '!');
});