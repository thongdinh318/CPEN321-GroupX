import {expect, test, jest} from "@jest/globals" ;
import { app, server,socket_server,wss } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { forum1, forum2, forum3, forum1_after, comment1, comment2_bad_forumId ,comment3_bad_userId} from "./testForum.js";
import {io} from "socket.io-client"


let connection
let fdb 
let db
let clientSockets= [];
let clientRes = [];


for(let i = 0; i < 3; i++)
    clientSockets[i] = io("http://localhost:9000");

initSockets();

beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    fdb = connection.db("ForumDB");
    db = connection.db("userdb");
    await fdb.collection("forums").deleteMany({})
    await db.collection("profile").insertOne({ 
        "userId": '0',
        "username": "root",
        "dob": null,
        "email":null,
        "subscriptionList":[],
        "history":[]
    })
    await fdb.collection('forums').insertOne({id: 1, name : "General News", comments : []});
    await fdb.collection('forums').insertOne({id: 2, name : "Economics", comments : []});
    await fdb.collection('forums').insertOne({id: 3, name : "Education", comments : []});
});



afterAll(async ()=>{
    // await fdb.collection("forums").deleteMany({})
    await connection.close();

    for(let i = 0; i < 3; i++)
        clientSockets[i].disconnect();
    
    server.close()
    socket_server.close()
});


//---------------------------------------------------------------
//Interface GET /forums
describe("GET /forums",()=>{
    //Chat GPT Usage: No
    test("Get all forums", async()=>{
        // Input: none
        // Expected status code: 200
        // Expected behavior: a list of forums is returned
        // Expected output: article_array
       
        const res = await supertest(app).get("/forums");
        expect(res.status).toBe(200);

        let resBody = res.body[0];
        delete resBody._id;
        // delete resBody.dateCreated; 
        expect(resBody).toStrictEqual(forum1);

        resBody = res.body[1];
        delete resBody._id;
        // delete resBody.dateCreated;
        expect(resBody).toStrictEqual(forum2);

        resBody = res.body[2];
        delete resBody._id;
        // delete resBody.dateCreated;
        expect(resBody).toStrictEqual(forum3);

        
    });

});

//Interface GET /forums/:forum_id
describe("GET /forums/:forum_id",  ()=>{
    //Chat GPT Usage: No
    
    it("Successfully retrieve forum from database", async ()=>{
        // Input: forum_id that is contained in database
        // Expected status code: 200
        // Expected behavior: forum is retrieved
        // Expected output: forum object
        
        const res = await supertest(app).get("/forums/1");
        expect(res.status).toBe(200);

        let resBody = res.body[0];
        delete resBody._id;
        // delete resBody.dateCreated; 
        expect(resBody).toStrictEqual(forum1);
        

    });

    //Chat GPT Usage: No
    test("forum_id not in database", async ()=>{
        // Input: forum_id that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing is retrieved from the database
        // Expected output: Error message saying “Forum does not exist”.
        
        const res = await supertest(app).get("/forums/900");
        expect(res.status).toBe(400);
        expect(res.text).toBe("Forum does not exist");
        
    });

});



describe("addComment sockets",  ()=>{
    //Chat GPT Usage: No

    it("Successfully post a comment to a forum", async()=>{
            // Input: forum_id that is contained in database, comment content, and the userId in the database
            // Expected status code: 
            // Expected behavior: comment is added to the identified forum and the updated forum is sent to all clients
            // Expected output: The updated forum

            clientSockets[0].emit("message", JSON.stringify(comment1));
            
            // Short delay because the code is too fast
            await new Promise(r => setTimeout(r, 200));

            const correctResponse = {                                                                                                                                                                     
                id: 1,
                name: 'General News',
                comments: [
                    { 
                        username: 'root', 
                        content: 'test comment for sockets' 
                    }
                ]
            };

            // Check that every other user has recieved the updated forum
            expect(JSON.stringify(clientRes[0])).toBe(JSON.stringify(clientRes[1]));
            expect(JSON.stringify(clientRes[0])).toBe(JSON.stringify(clientRes[2]));
            
            delete clientRes[0][0]._id;
            delete clientRes[0][0].dateCreated;
            delete clientRes[0][0].comments[0].datePosted;

            expect(JSON.stringify(clientRes[0][0])).toBe(JSON.stringify(correctResponse));
            
        });

        //Chat GPT Usage: No
        test("forum_id not in db: Can't post a comment to a forum", async()=>{
            // Input: forum_id that is not in database, comment content, and the userId in the database.
            // Expected status code: 
            // Expected behavior: Can't post comment.
            // Expected output: Error message saying: "Could not post comment"

            clientSockets[1].emit("message", JSON.stringify(comment2_bad_forumId));
            await new Promise(r => setTimeout(r, 200));
            expect(clientRes[1]).toBe("Could not post comment");

        });
        
        //Chat GPT Usage: No
        test("userId not in db: Can't post a comment to a forum", async()=>{
            // Input: forum_id that is contained in database, comment content, and a userId not in the database.
            // Expected status code: 
            // Expected behavior: Can't post comment.
            // Expected output: Error message saying: "Could not post comment"

            clientSockets[2].emit("message", JSON.stringify(comment3_bad_userId));
            await new Promise(r => setTimeout(r, 200));
            expect(clientRes[2]).toBe("Could not post comment");

        });

});


function initSockets(){

    for(let i = 0; i < 3; i++){
        clientSockets[i].on("new_message", (res)=>{
            clientRes[i] = res;
        })

        clientSockets[i].on("message_error", (res)=>{
            clientRes[i] = res;
        })
    }
   
}

