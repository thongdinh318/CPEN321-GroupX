import {expect, test} from "@jest/globals"
import { MongoClient } from "mongodb";
import {server, socket_server} from "../server.js"
import { bingNewsRetriever } from "../articles/retriever.js";

let connection
let db
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    
    db = connection.db("articledb");
});

afterAll(async ()=>{
    await db.collection('articles').deleteMany({});
    await connection.close()
    server.close()
    socket_server.close()
});

describe('test retriever', ()=>{
    //Chat GPT Usage: No
    test("empty query", async()=>{
        // Input: the query is empty
        // Expected behavior: a new article is retrieved
        // Expected output: array of 1 article
        const retrievedArticle = await bingNewsRetriever("")
        expect(retrievedArticle.length).toBe(1)
        expect(retrievedArticle[0].title).toStrictEqual("test html")
        expect(retrievedArticle[0].content).toStrictEqual("summary success")
    })
    //Chat GPT Usage: No
    test("retrieved article has multiple categories", async()=>{
        // Input: the query provided gives result to an article has multiple categories 
        // Expected behavior: a new article is retrieved
        // Expected output: array of 1 article

        const retrievedArticle = await bingNewsRetriever("1")
        expect(retrievedArticle.length).toBe(1)
        expect(retrievedArticle[0].title).toStrictEqual("test html")
        expect(retrievedArticle[0].content).toStrictEqual("summary success")
    })

    test("retrieved article has undefined category", async()=>{
        // Input: the query provided gives result to an article has undefined categories
        // Expected behavior: a new article is retrieved
        // Expected output: array of 1 article

        const retrievedArticle = await bingNewsRetriever("2")
        expect(retrievedArticle.length).toBe(1)
        expect(retrievedArticle[0].title).toStrictEqual("test html")
        expect(retrievedArticle[0].content).toStrictEqual("summary success")
    })
    //Chat GPT Usage: No
    test("empty html", async()=>{
        // Input: the query results in the endpoints find an article that cannot be scrapped,
        //        causes might be because the site has anti-bot measures implemented
        // Expected behavior: no article is retrieved
        // Expected output: empty array of articles

        const retrievedArticle = await bingNewsRetriever("empty")
        // console.log(retrievedArticle)
        expect(retrievedArticle.length).toBe(0)
    })
    //Chat GPT Usage: No
    test("error happened", async()=>{
        // Input: error happended when scrapping/ summarizing
        // Expected behavior: no article is retrieved
        // Expected output: empty array of articles
        const retrievedArticle = await bingNewsRetriever("error")
        expect(retrievedArticle.length).toBe(0)
    })
})
