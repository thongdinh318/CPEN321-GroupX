'use strict'
import axios from 'axios'
import cheerio from 'cheerio'
import * as summarizer from "./summaries.js"
import * as server from "../server.js"
import "dotenv/config.js"

const bing_endpoints = "https://api.bing.microsoft.com/v7.0/news"
const key = process.env.BingKey
console.log(key)
const EXCLUDED_SITE = ["-site:msn.com","-site:youtube.com", "-site:amazon.com"]
const FOCUSED_SITE = ["site:cbc.ca", "site:cnn.com"]
var id = 1 //keep track of article ids in the db
// const client = server.client
//ChatGPT usage: No
async function searchNews(query){
    var url = bing_endpoints+"/search"
    console.log(url)
    var user_query;
    if (query === ""){
        user_query = EXCLUDED_SITE.join(" ") + " " + FOCUSED_SITE.join(" OR ")
        // user_query += " " + FOCUSED_SITE.join(" OR ")
    }
    else{
        user_query = query + " " + EXCLUDED_SITE.join(" ") + " " + FOCUSED_SITE.join(" OR ")
        // user_query +=" " + FOCUSED_SITE.join(" OR ")
    }
    console.log(user_query)
    const res = await axios.get(url, {
        headers:{ 'Ocp-Apim-Subscription-Key': key},
        params:{
            q:user_query,
            count: 3,
            sortBy:"Date",
            freshness:"Day",
            mkt:'en-CA'
        }
    })
    return res.data

}


//ChatGPT usage: Yes
async function scrapeURL(url){
    var response = null
    try {
        response = await axios.get(url)

        if (response.status === 200){
            const html = response.data
            const $ = cheerio.load(html)

            var title = $('title').text();
            var para = [];

            $('p').each ((index, element)=>{
                para.push($(element).text());
            })
            var retrievedArticle = {
                title,
                para
            }
            return retrievedArticle
        }
    // else{
        var err = new Error()
        err.message = "Failed to retrieve"
        return err
    // }
    } catch (error) {
        return (error)
    }
}

// ChatGPT usage: No”
async function bingNewsRetriever(query){
    var result = await searchNews(query)
    if (query ==""){
        query = "general"
    }
    
    var retrievedArticles =[]
    for (var article of result.value){
        var articleEntry = {}
        articleEntry.url = article.url;
        var content = await scrapeURL(article.url)
        //skip if cannot scrape the content
        if (content.para == undefined || content.para.length === 0){
            continue
        }

        var webContent =""
        var sentenceNum = 0
        content.para.forEach((sentence) =>{
            sentence.replace('"',"'");
            webContent += sentence
            sentenceNum += 1 
        })
        var articleBody = await summarizer.summarizeArticle(webContent, Math.round(sentenceNum/2))
        if (articleBody && articleBody.e && articleBody.stack){
            continue
        }
        else{
            articleEntry.title = article.name
            articleEntry.content = articleBody
        }
        articleEntry.publisher = article.provider[0].name.toLowerCase()
        articleEntry.publishedDate = article.datePublished
        articleEntry.categories = article.category != undefined? [article.category]:[query]
        retrievedArticles.push(articleEntry)
    }
    addToDb(retrievedArticles)
    return retrievedArticles
}

// ChatGPT usage: No.
async function addToDb(articleList){
    for (var article of articleList){
        article.articleId = id
        id += 1
	console.log(article)
        await server.client.db("articledb").collection("articles").insertOne(article)
    }
}
// bingNewsRetriever("")
export {bingNewsRetriever}
