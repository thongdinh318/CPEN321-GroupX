import {expect, test, jest} from "@jest/globals" 
import { app } from "../server.js";
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
});

//interface GET /article/:articleId
describe('Get article info', async () => {
    // Input: articleId is a valid id
    // Expected status code: 200
    // Expected behavior: article is retrieved from the database
    // Expected output: article
    test('Valid id', async () => {
        const articleId = 1;
        const res = await supertest(app).get("/article/" + articleId)
        expect(res.status).toStrictEqual(200);
        expect(res.body).toStrictEqual(testArticle1)
    });

    // Input: articleId that is not contained in database
    // Expected status code: 400
    // Expected behavior: nothing is retrieved from the database
    // Expected output: error message: "Article Id Not Found"
    test('Invalid id', async () => {
        const articleId = 99;
        const res = await supertest(app).get("/article/" + articleId)
        expect(res.status).toStrictEqual(400);
        expect(res.text).toBe("Article Id Not Found");
    });
});


//interface GET /article/filter/search
describe('Search with filter', async () => {
    // Input: ‘filters’ are valid and there are matching articles
    // Expected status code: 200
    // Expected behavior: articles with matching filter
    // Expected output: array of matching articles
    test('Successful search', async () => {
        const searchQuery = "?publisher=CNN&after=2023-04-01&before=2023-12-31&categories=education,environment"
        const res = await supertest(app).get("/article/filter/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
    });

    // Input: ‘filters’ does not match with anything on database
    // Expected status code: 400
    // Expected behavior: articles not found, no articles is returned
    // Expected output: String saying "No articles matched"
    test('Filter not matching with anything', async () => {
        const searchQuery = "?publisher=FAKE&after=2023-04-01&before=2023-12-31&categories=education,environment"
        const res = await supertest(app).get("/article/" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.body).toStrictEqual([]);
    });
});

//interface GET /article/kwsearch/search
describe('Search with keywords', async () => {
    // Input: ‘keyword’ is a  valid keyword with matches
    // Expected status code: 200
    // Expected behavior: articles with matching keyword is returned
    // Expected output: article_array
    test('Successful search', async () => {
        const searchQuery = "?keyWord=match"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(200);
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
        expect(res.body).toStrictEqual([]);
    });

    // Input: ‘keyword’ does not match with anything on database
    // Expected status code: 400
    // Expected behavior: articles not found, no articles is returned
    // Expected output: String saying "No articles matched"
    test('Keywords not matching with anything', async () => {
        const searchQuery = "?keyWord=jibberjabber"
        const res = await supertest(app).get("/article/kwsearch/search" + searchQuery)
        expect(res.status).toStrictEqual(400);
        expect(res.body).toStrictEqual([]);
    });
});