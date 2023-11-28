import {expect, test, jest} from "@jest/globals" ;
import { app, server } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { forum1, forum2, forum3, forum1_after } from "./testForum.js";


let connection
let fdb
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    
    fdb = connection.db("ForumDB");
    // await fdb.collection("forums").insertMany([forum1, forum2, forum3]);
});

afterAll(async ()=>{
    // await fdb.collection('forums').deleteMany({});
    await connection.close();
    server.close()
});


//---------------------------------------------------------------
describe("GET /forums",()=>{

    test("Get all forums", async()=>{
        // Input: none
        // Expected status code: 200
        // Expected behavior: a list of forums is returned
        // Expected output: article_array
        const res = await supertest(app).get("/forums");
        expect(res.status).toBe(200);

        let resBody = res.body[0];
        delete resBody._id;
        delete resBody.dateCreated; // Because the date is accurate to the second
        expect(resBody).toStrictEqual(forum1);

        resBody = res.body[1];
        delete resBody._id;
        delete resBody.dateCreated;
        expect(res.body[1]).toStrictEqual(forum2);

        resBody = res.body[2];
        delete resBody._id;
        delete resBody.dateCreated;
        expect(res.body[2]).toStrictEqual(forum3);
        
    });

});


describe("GET /forums/:forum_id",  ()=>{
    
    test("Successfully retrieve forum from database", async ()=>{
        // Input: forum_id that is contained in database
        // Expected status code: 200
        // Expected behavior: forum is retrieved
        // Expected output: forum object

        const res = await supertest(app).get("/forums/1");
        expect(res.status).toBe(200);

        let resBody = res.body[0];
        delete resBody._id;
        delete resBody.dateCreated; 
        expect(resBody).toStrictEqual(forum1);

    });


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


describe("POST /addComment/:forum_id", ()=>{
        // Input: forum_id that is contained in database, comment content, and the userId in the database, and a null parent_id
        // Expected status code: 200
        // Expected behavior: comment is added to the identified forum as a reply or as a new post.
        // Expected output: The updated forum
        test("Successfully post a comment to a forum", async()=>{
            let test_comment = {
                userId : "0",
                parent_id : null,
                commentData : "test comment for POST /addComment"
                
            };
            const res = await supertest(app).post("/addComment/1").send(test_comment);
            expect(res.status).toBe(200);
            
            let resBody = res.body[0];
            delete resBody._id;
            delete resBody.dateCreated; 
            delete resBody["comments"][0].datePosted;
            delete resBody["comments"][0].comment_id;

            // console.log(resBody)

            expect(resBody["comments"][0].content).toStrictEqual(test_comment.commentData);
            expect(resBody).toStrictEqual(forum1_after);

            await fdb.collection('forums').updateOne({ id : 1},{ $set:{ comments : [] }} );
        });



        // Input: forum_id that is not in database, comment content, the userId in the database and a null parent_id.
        // Expected status code: 500
        // Expected behavior: Can't post comment.
        // Expected output: Error message saying: "Could not post comment"
        test("forum_id not in db: Can't post a comment to a forum", async()=>{
            let test_comment = {
                userId : "0", 
                parent_id : null,
                commentData : "test comment for POST /addComment"
            };
            
                                                // invalid forum_id
            const res = await supertest(app).post("/addComment/900").send(test_comment);
            expect(res.status).toBe(500);
            expect(res.text).toBe("Could not post comment");

        });



        // Input: forum_id that is contained in database, comment content, a userId not in the database and a null parent_id.
        // Expected status code: 500
        // Expected behavior: Can't post comment.
        // Expected output: Error message saying: "Could not post comment: Invalid UserId"
        test("userId not in db: Can't post a comment to a forum", async()=>{
            let test_comment = {
                userId : "100", // inalid id
                parent_id : null,
                commentData : "test comment for POST /addComment"
            };

            const res = await supertest(app).post("/addComment/1").send(test_comment);
            expect(res.status).toBe(500);
            expect(res.text).toBe("Could not post comment: Invalid UserId");

        });





        // Input: 9 inputs containing: forum_id that is contained in database, comment content, and the userId in the database, 
        //          and non-null parent_id, and 1 with a null parent_id.
        // Expected status code: 200
        // Expected behavior: comments are added to the identified forum as a reply or as a new post with the correct level.
        // Expected output: The updated forum
        test("Successfully post a comment to a forum", async()=>{

            const max_commentLevel = 3;
            let parent_id = null;
            let expected_parent_id = null;
            let resBody;

            for(let i = 0; i < 10; i++){
                resBody = await postCommentTest(expected_parent_id);
                parent_id = resBody["comments"][i].parent_id;
                let commentLevel = resBody["comments"][i].commentLevel;
                
                if(i < max_commentLevel + 1){
                    expect(parent_id).toBe(expected_parent_id);
                    expect(commentLevel).toBe(i);

                } else{

                    expect(parent_id).toBe(expected_parent_id);
                    expect(commentLevel).toBe(max_commentLevel);
                }


                if(i < max_commentLevel)
                    expected_parent_id = resBody["comments"][i].comment_id;
            }

            // console.log(resBody);

            await fdb.collection('forums').updateOne({ id : 2},{ $set:{ comments : [] }} );
        });

});


async function  postCommentTest(parent_id ){

    let test_comment = {
        userId : "0",
        parent_id : parent_id,
        commentData : "testing child comments"
    };

    const res = await supertest(app).post("/addComment/2").send(test_comment);
            
    expect(res.status).toBe(200);
    
    let resBody = res.body[0];
    delete resBody._id;
    delete resBody.dateCreated; 
    delete resBody["comments"][0].datePosted;

    return resBody;
}