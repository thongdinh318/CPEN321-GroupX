'use strict'
import axios from 'axios'
import cheerio from 'cheerio'
import { summarizeArticle } from './summaries.js'
import { MongoClient } from "mongodb";

const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

const bing_endpoints = "https://api.bing.microsoft.com/v7.0/news"
const key = '56e2a381b452440abe22ce6ffb33b485'
const EXCLUDED_SITE = ["-site:msn.com","-site:youtube.com", "-site:amazon.com"]
const FOCUSED_SITE = ["site:cbc.ca", "site:cnn.com"]
var id = 1 //keep track of article ids in the db

async function searchNews(query){
    var url = bing_endpoints+"/search"
    console.log(url)
    try {
        var user_query;
        if (query === ""){
            user_query = EXCLUDED_SITE.join(" ")
            // user_query += " " + FOCUSED_SITE.join(" OR ")
        }
        else{
            user_query = query + " " + EXCLUDED_SITE.join(" ")
            // user_query +=" " + FOCUSED_SITE.join(" OR ")
        }
        console.log(user_query)
        const res = await axios.get(url, {
            headers:{ 'Ocp-Apim-Subscription-Key': key},
            params:{
                q:user_query,
                count: 5,
                sortBy:"Date",
                freshness:"Day",
                mkt:'en-CA'
            }
        })
        return res.data
    } catch (error) {
        throw error
    }
}

async function scrapeURL(url){
    try {
        const response = await axios.get(url)

        if (response.status === 200){
            const html = response.data
            const $ = cheerio.load(html)

            var title = $('title').text();
            var para = [];

            $('p').each ((index, element)=>{
                para.push($(element).text());
            })
            return {title, para}
        }
        else{
            return {error:"Failed to retrieve"}
        }
    } catch (error) {
        return {error: error.message}
    }
}

async function bingNewsRetriever(query){
    var result = await searchNews(query)
    
    var retrievedArticles =[]
    for (var article of result.value){
        var articleEntry = new Object()
        var content = await scrapeURL(article.url)
        //skip if cannot scrape the content
        if (content.para == undefined || content.para.length == 0){
            continue
        }

        var webContent =""
        var sentenceNum = 0
        content.para.forEach((sentence) =>{
            sentence.replace('"',"'");
            webContent += sentence
            sentenceNum += 1 
        })
        var articleBody = await summarizeArticle(webContent, Math.round(sentenceNum/2))
        if (articleBody, articleBody.e, articleBody.stack){
            continue
        }
        else{
            articleEntry.content = articleBody
        }
        articleEntry.publisher = article.provider[0].name
        articleEntry.publishedDate = article.datePublished
        articleEntry.categories = article.category != undefined? [article.category]:[query]
        articleEntry.title = article.name
        retrievedArticles.push(articleEntry)
    }

    console.log(retrievedArticles)
    addToDb(retrievedArticles)
    return retrievedArticles
}

async function addToDb(articleList){
    for (var article of articleList){
        article.articleId = id
        id += 1
        await client.db("articledb").collection("articles").insertOne(article)
    }
}
// bingNewsRetriever("")
export {bingNewsRetriever}