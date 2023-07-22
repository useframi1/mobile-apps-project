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

// API: get user
// Method: GET
srv.get("/getUser", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query
    const username = q.username;

    var sql = "SELECT * FROM Users WHERE username = ?";

    // execute the sql command
    connection.query(sql, username, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("user fetched");
        res.send(result);
    });
});

// API: get user
// Method: GET
srv.get("/getAllUsers", function (req, res) {
    var q = url.parse(req.url, true).query;
    const username = q.username;

    var sql = "SELECT * FROM Users WHERE username != ?";

    // execute the sql command
    connection.query(sql, username, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("users fetched");
        res.send(result);
    });
});

// API: create user
// Method: POST
srv.post("/createUser", function (req, res) {
    const { username, name, email, age, bio } = req.body; // parse json body

    var sql =
        "INSERT INTO Users (username, name, email, age, bio) VALUES (?, ?, ?, ?, ?)"; // sql command to insert a record in the database

    // execute the sql command
    connection.query(sql, [username, name, email, age, bio], function (err) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("User record inserted");
        res.send("1");
    });
});

// curl -X POST -H "Content-Type: application/json" -d '{"username":"useframi","name":"youssef rami","bio":"hello world","age":"20","email":"yousseframi@aucegypt.edu"}' http://localhost:3000/createUser

// API: get requests
// Method: GET
srv.get("/getRequests", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.creator;

    var sql =
        "SELECT M.ID, M.mDate , M.sport , M.startTime , M.endTime , R.username, R.seen FROM Meetings M INNER JOIN Requests R ON M.ID = R.ID WHERE M.creator = ?";

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

srv.post("/addRequest", function (req, res) {
    const { ID, username } = req.body;

    var sql = "INSERT INTO Requests (ID, username) VALUES (?,?)";
    connection.query(sql, [ID, username], function (err) {
        if (err) {
            res.send("0");
            throw err;
        }
        console.log("Meeting request inserted");
    });
    res.send("1");
});
// API: get individual meetings
// Method: GET
srv.get("/getMeetings", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT m.* FROM Meetings m INNER JOIN Requests r ON m.ID = r.ID WHERE m.creator <> ? AND m.partner IS NULL AND NOT EXISTS (SELECT 1 FROM Requests r2 WHERE r2.ID = m.ID AND r2.username = ?);";
    // execute sql command
    connection.query(sql, [username, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// API: get group meetings
// Method: GET
srv.get("/getGroups", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.creator;

    var sql = "SELECT * FROM GroupMeetings WHERE creator != ?";
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

srv.get("/getGroupMembers", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const ID = q.ID;

    var sql = "SELECT username FROM GroupMembers WHERE ID = ?";

    connection.query(sql, ID, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// API: create group
// Method: POST
srv.post("/createGroup", function (req, res) {
    const { creator, name, sport, startTime, endTime, gDate } = req.body; // parse json body
    var id = 0;
    var sql =
        "INSERT INTO GroupMeetings (creator, name, sport, startTime, endTime, gDate) VALUES (?, ?, ?, ?, ?, ?)"; // sql command to insert a record in the database

    console.log(creator);
    console.log(
        "====================================================================="
    );

    connection.query(
        sql,
        [creator, name, sport, startTime, endTime, gDate],
        function (err, result) {
            if (err) {
                // res.send("0");
                throw err;
            }
            id = result.insertId;

            var sql = "INSERT INTO GroupMembers (ID, username) VALUES (?,?)";
            connection.query(sql, [id, creator], function (err, result) {
                if (err) {
                    // res.send("0");
                    throw err;
                }
            });
            res.send(result.insertId.toString());
        }
    );
});

// API; add group members
// Method: POST
srv.post("/addGroupMembers", function (req, res) {
    const { ID, groupMembers } = req.body;

    for (let i = 0; i < groupMembers.length; i++) {
        const member = groupMembers[i];
        var sql = "INSERT INTO GroupMembers (ID, username) VALUES (?,?)";
        connection.query(sql, [ID, member], function (err) {
            if (err) {
                res.send("0");
                throw err;
            }

            console.log("Group members record inserted");
        });
    }
    res.send("1");
});

// API: create meeting
// Method: POST
srv.post("/createMeeting", function (req, res) {
    const { creator, sport, startTime, endTime, mDate } = req.body; // parse json body

    var sql =
        "INSERT INTO Meetings (creator, sport, startTime, endTime, mDate) VALUES (?, ?, ?, ?, ?)"; // sql command to insert a record in the database

    // execute the sql command
    connection.query(
        sql,
        [creator, sport, startTime, endTime, mDate],
        function (err, result) {
            if (err) {
                res.send("0");
                throw err;
            }

            console.log("Group meeting record inserted");
            res.send(result.insertId.toString());
        }
    );
});

// API: get created meetings
// Method: GET
srv.get("/getCreatedMeetings", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT ID, mDate, sport, startTime, endTime, partner FROM Meetings WHERE creator = ?";
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

// API: get created groups
// Method: GET
srv.get("/getCreatedGroups", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT ID, gDate, sport, startTime, endTime, name FROM GroupMeetings WHERE creator = ?";
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

// API: get accepted meetings
// Method: GET
srv.get("/getAcceptedMeetings", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT M.ID, M.mDate , M.sport , M.startTime , M.endTime , M.creator, M.partner FROM Meetings M INNER JOIN Requests R ON M.ID = R.ID WHERE R.username = ?";

    // execute sql command
    connection.query(sql, username, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        console.log(result);
        res.send(result);
    });
});

// API: get joined groups
// Method: GET
srv.get("/getJoinedGroups", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT g.ID FROM GroupMeetings g INNER JOIN GroupMembers m ON g.ID = m.ID WHERE m.username = ? AND g.creator != ?";

    // execute sql command
    connection.query(sql, [username, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// API: get joined groups
// Method: GET
srv.get("/getJoinedGroups", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql =
        "SELECT g.ID FROM GroupMeetings g INNER JOIN GroupMembers m ON g.ID = m.ID WHERE m.username = ? AND g.creator != ?";

    // execute sql command
    connection.query(sql, [username, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// API: update user
// Method: POST
srv.post("/updateUser", function (req, res) {
    const { username, newUsername, name, bio } = req.body; // parse json body

    var sql =
        "UPDATE Users SET username = ?, name = ?, bio = ? WHERE username = ?";

    // execute sql command
    connection.query(
        sql,
        [newUsername, name, bio, username],
        function (err, result) {
            if (err) {
                res.send("0");
                throw err;
            }

            console.log("Request recieved");
        }
    );

    var sql = "DELETE FROM PreferredSports WHERE username = ?";
    connection.query(sql, newUsername, function (err) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Preferred sports deleted");
    });

    res.send("1");
});

// API: add preferred sports
// Method: POST
srv.post("/addPreferredSports", function (req, res) {
    const { username, preferredSports } = req.body;

    for (let i = 0; i < preferredSports.length; i++) {
        const sport = preferredSports[i];
        var sql = "INSERT INTO PreferredSports (username, sport) VALUES (?,?)";
        connection.query(sql, [username, sport], function (err) {
            if (err) {
                res.send("0");
                throw err;
            }

            console.log("Preferred sport record inserted");
        });
    }
    res.send("1");
});

srv.get("/getPreferredSports", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const username = q.username;

    var sql = "SELECT sport FROM PreferredSports WHERE username = ?";
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

// API: update partner
// Method: POST
srv.post("/updatePartner", function (req, res) {
    const { ID, partner } = req.body;

    var sql = "UPDATE Meetings SET partner = ? WHERE ID = ?";

    // execute sql command
    connection.query(sql, [partner, ID], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });

    sql = "DELETE FROM Requests WHERE ID = ?";

    // execute sql command
    connection.query(sql, [ID, partner], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Deleted requests");
        res.send(result);
    });
});

// API: delete Meeting
// Method: GET
// srv.get("/deleteMeeting", function (req, res) {

// });

// API: get chats
// Method: GET

// API: get username using email
// Method: GET
srv.get("/getUsername", function (req, res) {
    var q = url.parse(req.url, true).query; // parse the url to get the query

    const email = q.email;

    var sql = "SELECT username FROM Users WHERE email = ?";
    // execute sql command
    connection.query(sql, email, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

srv.post("/cancelMeeting", function (req, res) {
    const { ID } = req.body;

    var sql = "DELETE FROM Meetings WHERE ID = ?";

    connection.query(sql, ID, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

srv.post("/cancelRequest", function (req, res) {
    const { ID, username } = req.body;

    var sql = "DELETE FROM Requests WHERE ID = ? AND username = ?";

    connection.query(sql, [ID, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

srv.post("/deleteGroup", function (req, res) {
    const { ID } = req.body;

    var sql = "DELETE FROM GroupMeetings WHERE ID = ?";

    connection.query(sql, ID, function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

srv.post("/leaveGroup", function (req, res) {
    const { ID, username } = req.body;

    var sql = "DELETE FROM GroupMembers WHERE ID = ? AND username = ?";

    connection.query(sql, [ID, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

srv.post("/requestStatus", function (req, res) {
    const { IDs } = req.body;

    var sql = "UPDATE Requests SET seen = 1 WHERE ID = ?";
    for (let i = 0; i < IDs.length; i++) {
        connection.query(sql, IDs[i], function (err) {
            if (err) {
                res.send("0");
                throw err;
            }
            console.log("Request recieved");
        });
    }
});

srv.post("/kick", function (req, res) {
    const { ID, username } = req.body;

    var sql = "DELETE FROM GroupMembers WHERE ID = ? AND username = ?";

    connection.query(sql, [ID, username], function (err, result) {
        if (err) {
            res.send("0");
            throw err;
        }

        console.log("Request recieved");
        res.send(result);
    });
});

// make server listen for connections at port 3000
srv.listen(3000, function () {
    console.log("Server is listening on port 3000.");
});
