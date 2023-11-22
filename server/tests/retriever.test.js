import {expect, test, jest} from "@jest/globals"
import { MongoClient } from "mongodb";
import {server} from "../server.js"
import { bingNewsRetriever } from "../articles/retriever.js";

let connection
let db
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    
    db = connection.db("articledb");
});

afterAll(async ()=>{
    await db.collection('articledb').deleteMany({});
    await connection.close()
    server.close()
});

describe('test retriever', ()=>{
    test("empty query", async()=>{
        // Input: the query is empty
        // Expected behavior: a new article is retrieved
        // Expected output: array of 1 article
        const retrievedArticle = await bingNewsRetriever("")
        expect(retrievedArticle.length).toBe(1)
        expect(retrievedArticle[0].title).toStrictEqual("test html")
        expect(retrievedArticle[0].content).toStrictEqual("summary success")
    })

    test("nonempty query", async()=>{
        // Input: the query is not empty
        // Expected behavior: a new article is retrieved
        // Expected output: array of 1 article

        const retrievedArticle = await bingNewsRetriever("abc")
        expect(retrievedArticle.length).toBe(1)
        expect(retrievedArticle[0].title).toStrictEqual("test html")
        expect(retrievedArticle[0].content).toStrictEqual("summary success")
    })

    test("empty html", async()=>{
        // Input: the query results in the endpoints find an article that cannot be scrapped,
        //        causes might be because the site has anti-bot measures implemented
        // Expected behavior: no article is retrieved
        // Expected output: empty array of articles

        const retrievedArticle = await bingNewsRetriever("empty")
        // console.log(retrievedArticle)
        expect(retrievedArticle.length).toBe(0)
    })

    test("error happened", async()=>{
        // Input: error happended when scrapping/ summarizing
        // Expected behavior: no article is retrieved
        // Expected output: empty array of articles
        const retrievedArticle = await bingNewsRetriever("error")
        expect(retrievedArticle.length).toBe(0)
    })
})
