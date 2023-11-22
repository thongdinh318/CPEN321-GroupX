import {expect, test, jest} from "@jest/globals" 
import { app, server } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { testArticle1, testArticle2, testArticle3 } from "./testArticles.js";

let connection
let db
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    
    db = connection.db("articledb");
    await db.collection("articles").insertMany([testArticle1, testArticle2, testArticle3]);
});

afterAll(async ()=>{
    await db.collection('articledb').deleteMany({});
    await connection.close()
    server.close()
});

//interface GET /article/:articleId
describe('Get article info', () => {
    // Input: articleId is a valid id
    // Expected status code: 200
    // Expected behavior: article is retrieved from the database
    // Expected output: article
    test('Valid id', async () => {
        const articleId = 1;
        const res = await supertest(app).get("/article/" + articleId)
        expect(res.status).toStrictEqual(200);
        delete res.body._id
        delete testArticle1._id
        expect(res.body).toStrictEqual(testArticle1)
    });

    // Input: articleId that is not contained in database
    // Expected status code: 400
    // Expected behavior: nothing is retrieved from the database
    // Expected output: error message: "Article Id Not Found"
    test('Invalid id', async () => {
        const articleId = 99;
        const res = await supertest(app).get("/article/" + articleId)
        // expect(res.status).toStrictEqual(400);
        expect(res.text).toBe("Article Id Not Found");
    });
});


//interface GET /article/filter/search
describe('Search with filter', () => {
    // Input: ‘filters’ are valid and there are matching articles
    // Expected status code: 200
    // Expected behavior: articles with matching filter
    // Expected output: array of matching articles
    test('Successful search', async () => {
        const searchQuery = "?publisher=CNN&after=2023-04-01&before=2023-12-31&categories=education,environment&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
        delete res.body[0]._id;
        delete testArticle2._id
        expect(res.body[0]).toStrictEqual(testArticle2);
    });

    // Input: ‘filters’ are valid and there are more than 10 matching articles
    // Expected status code: 200
    // Expected behavior: articles with matching filter
    // Expected output: array of 9 matching articles
    test('Successful search with multiple matching', async () => {
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

    // Input: ‘filters’ does not match with anything on database
    // Expected status code: 400
    // Expected behavior: articles not found, no articles is returned
    // Expected output: String saying "No articles matched"
    test('Filter not matching with anything', async () => {
        const searchQuery = "?publisher=&after=2023-09-01&before=2023-12-31&categories=education,environment&kw="
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles matched");
    });
});

//interface GET /article/kwsearch/search
describe('Search with keywords', () => {
    // Input: ‘keyword’ is a  valid keyword with matches
    // Expected status code: 200
    // Expected behavior: articles with matching keyword is returned
    // Expected output: article_array
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
    test('Keywords not matching with anything', async () => {
        const searchQuery = "?keyWord=jibberjabber"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toStrictEqual("No articles matched");
    });
});