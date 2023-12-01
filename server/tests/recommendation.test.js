import {expect, test, jest} from "@jest/globals" 
import { app, server, socket_server } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { testUser1, testUser2, testUser3, testUser4 } from "./testUsers.js";
import { testArticle1, testArticle2, testArticle3 } from "./testArticles.js";
import * as userMod from "../user.js"

let connection
let aDB, uDB
beforeAll(async ()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    // fdb = connection.db("ForumDB");
    uDB = connection.db("userdb");
    aDB = connection.db("articledb");
    await uDB.collection("profile").insertMany([testUser1, testUser2, testUser3, testUser4]);
    await aDB.collection("articles").insertMany([testArticle1, testArticle2, testArticle3]);  
})

afterAll(async ()=>{
    await uDB.collection('profile').deleteMany({});
    await aDB.collection('articles').deleteMany({});
    await connection.close()
    server.close()
    socket_server.close()
})

//Interface GET /recommend/article/:userId
describe("GET /recommend/article/:userId", ()=>{
    //Chat GPT Usage: No
    test('user havent view anything', async ()=>{
        // Input: userId of user that has not viewed anything
        // Expected status code: 200
        // Expected behavior: recommend all articles in the db
        // Expected output: array of all articles in the db (3 for testing)
        const res = await supertest(app).get("/recommend/article/3");
        expect(res.status).toBe(200);
        expect(res.body.length).toStrictEqual(3);
    });
    //Chat GPT Usage: No
    test('user have viewed some of the articles', async ()=>{
        // Input: userId of user that has viewed some of the articles
        // Expected status code: 200
        // Expected behavior: recommened articles that the user has not viewed
        // Expected output: articles that the user has not viewed

        const res = await supertest(app).get("/recommend/article/2");
        expect(res.status).toBe(200);
        delete res.body[0]._id
        delete testArticle3._id
        expect(res.body[0]).toStrictEqual(testArticle3);
    });
    //Chat GPT Usage: No
    test('user have viewed all of the articles', async ()=>{
        // Input: userId of user that has viewed some of the articles
        // Expected status code: 200
        // Expected behavior: not recommend anything for this user
        // Expected output: empty array

        const res = await supertest(app).get("/recommend/article/1");
        expect(res.status).toBe(200);
        expect(res.body.length).toStrictEqual(0);
    });
    //Chat GPT Usage: No
    test('invalid userId', async ()=>{
        // Input: userId of user that has viewed some of the articles
        // Expected status code: 400
        // Expected behavior: return error message
        // Expected output: a message string: "User not Found"

        const res = await supertest(app).get("/recommend/article/13");
        expect(res.status).toBe(400);
        expect(res.text).toStrictEqual("User not Found");
    });
});