var mysql = require("mysql");
var express = require("express");
var url = require("url");
const bodyParser = require("body-parser");
var srv = express();

srv.use(bodyParser.json());

// create a mysql connection to the database
var connection = mysql.createConnection({
    host: "db4free.net",
    user: "gymwya",
    password: "gymwya12345",
    database: "gymwya",
});

// start connection to the database
connection.connect(function (err) {
    if (err) throw err;
    console.log("Connected");
});

// API: create user
// Method: POST
srv.post("/createUser", function (req, res) {
    const { username, name, bio, age, email } = req.body; // parse json body

    var sql =
        "INSERT INTO UserAccount (username, name, bio, age, email) VALUES (?, ?, ?, ?, ?)"; // sql command to insert a record in the database

    // execute the sql command
    connection.query(sql, [username, name, bio, age, email], function (err) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("1 record inserted");
        res.send("1");
    });
});

// curl -X POST -H "Content-Type: application/json" -d '{"username":"useframi","name":"youssef rami","bio":"hello world","age":"20","email":"yousseframi@aucegypt.edu"}' http://localhost:3000/createUser

// API: get requests
// Method: GET
srv.get("/getRequests", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.reciever;

    var sql = "SELECT * from Request WHERE reciever = ?";

    // execute sql command
    connection.query(sql, username, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// API: get individual meetings
// Method: GET
srv.get("/getMeetings", function (req, res) {});

// API: get group meetings
// Method: GET

// API: create group
// Method: POST

// API: create meeting
// Method: POST

// API: get current matches
// Method: GET

// API: update user
// Method: POST

// API: get chats
// Method: GET

// make server listen for connections at port 3000
srv.listen(3000, function () {
    console.log("Server is listening on port 3000.");
});
