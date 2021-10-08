var express = require("express");
const cors = require('cors');
var app = express();

app.use(cors())
app.listen(3001, () => {
 console.log("Server running on port 3001");
});
app.get("/hello", (req, res, bext) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.json("helloo");
});