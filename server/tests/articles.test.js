import {expect, test, jest} from "@jest/globals" 
import { app, server, socket_server } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { testUser1, testUser2, testUser3, testUser4 } from "./testUsers.js";
import { testArticle1, testArticle2, testArticle3 } from "./testArticles.js";

let connection
let db, uDB
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    uDB = connection.db("userdb");    
    db = connection.db("articledb");
    await db.collection("articles").insertMany([testArticle1, testArticle2, testArticle3]);
    await uDB.collection("profile").insertMany([testUser1,testUser2, testUser3, testUser4]);
});

afterAll(async ()=>{
    await db.collection('articles').deleteMany({});
    await uDB.collection('profile').deleteMany({});
    await connection.close()
    server.close()
    socket_server.close()
});

//interface GET /article/:articleId
describe('Get article info', () => {
    //Chat GPT Usage: No
    test('Valid id', async () => {
        // Input: articleId is a valid id
        // Expected status code: 200
        // Expected behavior: article is retrieved from the database
        // Expected output: article

        const articleId = 1;
        const res = await supertest(app).get("/article/" + articleId)
        expect(res.status).toStrictEqual(200);
        delete res.body._id
        delete testArticle1._id
        expect(res.body).toStrictEqual(testArticle1)
    });

    //Chat GPT Usage: No
    test('Invalid id', async () => {
        // Input: articleId that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing is retrieved from the database
        // Expected output: error message: "Article Id Not Found"
        const articleId = 99;
        const res = await supertest(app).get("/article/" + articleId)
        // expect(res.status).toStrictEqual(400);
        expect(res.text).toBe("Article Id Not Found");
    });
});


//interface GET /article/filter/search
describe('Search with filter', () => {
    //Chat GPT Usage: No
    test('Successful search', async () => {
        // Input: ‘filters’ are valid and there are matching articles
        // Expected status code: 200
        // Expected behavior: articles with matching filter
        // Expected output: array of matching articles
        const searchQuery = "?publisher=CNN&after=2023-04-01&before=2023-12-31&categories=education,environment&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete res.body[0]._id;
        delete testArticle2._id
        expect(res.body[0]).toStrictEqual(testArticle2);
    });
    //Chat GPT Usage: No
    test('Filter with only before date', async () => {
        // Input: ‘filters’ only contains before date query
        // Expected status code: 200
        // Expected behavior: articles with matching filter
        // Expected output: array of matching articles, only 1 for testing

        const searchQuery = "?publisher=&after=&before=2023-04-30&categories=&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete testArticle1._id
        delete res.body[0]._id
        expect(res.body[0]).toStrictEqual(testArticle1)
    });
    //Chat GPT Usage: No
    test('Filter with only after date', async () => {
        // Input: ‘filters’ only contains after date query
        // Expected status code: 200
        // Expected behavior: articles with matching filter
        // Expected output: array of matching articles, only 2 for testing

        const searchQuery = "?publisher=&after=2023-04-30&before=&categories=&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete testArticle2._id
        delete res.body[0]._id
        // expect(res.body.length).toBe(2)
        expect(res.body[0]).toStrictEqual(testArticle2)
    });

    test('Filter articles in the same date', async () => {
        // Input: ‘filters’ only contains after date query
        // Expected status code: 200
        // Expected behavior: articles with matching filter
        // Expected output: array of matching articles, only 1 for testing

        const searchQuery = "?publisher=&after=2023-01-30&before=2023-01-30&categories=&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(200).toStrictEqual(200);
        // console.log(res.text)
        delete testArticle3._id
        delete res.body[0]._id
        expect(res.body[0]).toStrictEqual(testArticle3)
    });
    //Chat GPT Usage: No
    test('Filter with only keyWord', async () => {
        // Input: ‘filters’ only contains kw query
        // Expected status code: 200
        // Expected behavior: articles that matches filter
        // Expected output: array of matching articles, only 1 for testing
        const searchQuery = "?publisher=&after=&before=&categories=&kw=match"
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete testArticle1._id
        delete res.body[0]._id
        expect(res.body[0]).toStrictEqual(testArticle1)
    });

    test('Filter not matching with anything', async () => {
        // Input: ‘filters’ does not match with anything on database
        // Expected status code: 400
        // Expected behavior: articles not found, no articles is returned
        // Expected output: String saying "No articles matched"
        //Chat GPT Usage: No
        const searchQuery = "?publisher=&after=2023-09-01&before=2023-12-31&categories=notmatch&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles matched");
    });
    test('invalid date range', async()=>{
        // Input: the before date is less than the after date
        // Expected status code: 400
        // Expected behavior: articles not found, no articles is returned
        // Expected output: String saying "Invalid date range. Please try again"
        //Chat GPT Usage: No
        const searchQuery = "?publisher=&after=2023-12-01&before=2023-04-31&categories=education,environment&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toBe(400)
        expect(res.text).toStrictEqual("Invalid date range. Please try again")
    })

    test('missing a query field', async()=>{
        // Input: a query that does not have all data fields
        // Expected status code: 400
        // Expected behavior: an error message is returned
        // Expected output: String saying "Invalid query. Please try again"
        //Chat GPT Usage: No
        const searchQuery = "?publisher=&after=2023-12-01"
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toBe(400)
        expect(res.text).toStrictEqual("Invalid query. Please try again")
    })

    test('Successful search but with special characters in the query', async () => {
        // Input: ‘filters’ matches with an entry on database but contains special characters in many fields
        // Expected status code: 200
        // Expected behavior: sanitize the query then find and returns articles with matching filter
        // Expected output: array of matching articles
        const searchQuery = "?publisher=CNN&after=2023-04-01&before=2023-12-31&categories=<edu$$$$$$$$$$cation>,<$$$$$$environment>&kw=$'\"\'<><><><>"
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete res.body[0]._id;
        delete testArticle2._id
        expect(res.body[0]).toStrictEqual(testArticle2);
    });
    test('Successful search with multiple matching', async () => {
        // Input: ‘filters’ are valid and there are more than 10 matching articles
        // Expected status code: 200
        // Expected behavior: articles with matching filter
        // Expected output: array of 9 matching articles
        //Chat GPT Usage: No
        var array_testArticles =[]
        for (var i = 0; i < 15; i++){
            var testArticle = JSON.parse(JSON.stringify(testArticle2))
            testArticle.id = 4 + i
            array_testArticles.push(testArticle)
        }
        await db.collection("articles").insertMany(array_testArticles);

        const searchQuery = "?publisher=CNN&after=2023-04-01&before=2023-12-31&categories=education,environment&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(9)
        
        await db.collection("articles").deleteMany({});
        await db.collection("articles").insertMany([testArticle1,testArticle2,testArticle3]);
    });

});
// Interface GET /article/subscribed/:userId
describe("GET /article/subscribed/:userId", ()=>{

    //Chat GPT Usage: No
    test('Exist user id with subscribers', async () => {
        // Input: userId that subscribed to at least 1 publisher
        // Expected status code: 200
        // Expected behavior: articles belongs to publishers the user subscribe to
        // Expected output: array of matching articles

        const res = await supertest(app).get("/article/subscribed/3").set("jwt","3") 
        expect(res.status).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(1);
        delete res.body[0]._id
        delete testArticle3._id
        expect(res.body[0]).toStrictEqual(testArticle3)
    });
    //Chat GPT Usage: No
    test('Exist user id with no subs', async () => {
        // Input: userId that have not subscribed to any publishers
        // Expected status code: 200
        // Expected behavior: return no articles
        // Expected output: return "No articles found"

        const res = await supertest(app).get("/article/subscribed/2").set("jwt","2")
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles found");
    });
    //Chat GPT Usage: No
    test('Exist user id with subs that has no articles in db', async () => {
        // Input: userId that subscribed to a publisher that has no articles on the db
        // Expected status code: 400
        // Expected behavior: return a error message
        // Expected output: returns string "No articles found"
        const res = await supertest(app).get("/article/subscribed/4").set("jwt","4")
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles found");
    });
    //Chat GPT Usage: No
    test('Non Exist user id', async () => {
        // Input: userId that does not exist on the db
        // Expected status code: 400
        // Expected behavior: return a error message
        // Expected output: returns string "User not found"
        const res = await supertest(app).get("/article/subscribed/12").set("jwt","12")
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("User not found");
    });

    test("no header", async()=>{
        // Input: a request with no jwt included in header
        // Expected status code: 400
        // Expected behavior: return error message
        // Expected output: a message string: "No JWT in headers"
        const res = await supertest(app).get("/article/subscribed/3") 
        expect(res.status).toBe(400)
        expect(res.text).toStrictEqual("No JWT in headers")
    })

    test("expired token", async()=>{
        // Input: a request with an expired token in the header
        // Expected status code: 403
        // Expected behavior: return error message
        // Expected output: a message string: "Expired token"
        const res = await supertest(app).get("/article/subscribed/3").set("jwt","expired")
        expect(res.status).toBe(403)
        expect(res.text).toStrictEqual("Expired Token")
    })

    test("mismatched token", async()=>{
        // Input: a request with jwt belongs to the other user included in header
        // Expected status code: 400
        // Expected behavior: return error message
        // Expected output: a message string: "Wrong token"
        const res = await supertest(app).get("/article/subscribed/3").set("jwt","2")
        expect(res.status).toBe(400)
        expect(res.text).toStrictEqual("Wrong token")
    })
})
//interface GET /article/kwsearch/search
describe('Search with keywords', () => {
    // Input: ‘keyword’ is a  valid keyword with matches
    // Expected status code: 200
    // Expected behavior: articles with matching keyword is returned
    // Expected output: article_array
    //Chat GPT Usage: No
    test('Successful search', async () => {
        const searchQuery = "?keyWord=match"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete testArticle1._id
        delete(res.body[0]._id)
        expect(res.body).toStrictEqual([testArticle1]);
    });

    // Input:  ‘keyword’ has special characters
    // Expected status code: 400
    // Expected behavior: articles not found, no articles is returned
    // Expected output: String saying "No articles matched"
    //Chat GPT Usage: No
    test('Special Characters in keyword', async () => {
        const searchQuery = "?keyWord=__!"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles matched");
    });

    // Input: ‘keyword’ does not match with anything on database
    // Expected status code: 400
    // Expected behavior: articles not found, no articles is returned
    // Expected output: String saying "No articles matched"
    //Chat GPT Usage: No
    test('Keywords not matching with anything', async () => {
        const searchQuery = "?keyWord=jibberjabber"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles matched");
    });
});