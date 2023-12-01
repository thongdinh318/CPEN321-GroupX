'use strict'
import axios from 'axios'
import cheerio from 'cheerio'
import * as summarizer from "./summaries.js"
import * as server from "../server.js"
import "dotenv/config.js"

const bing_endpoints = "https://api.bing.microsoft.com/v7.0/news"
var key =""
if (process.env.BingKey == undefined){
  key = "BingKey";

}
else{
  key = process.env.BingKey
}
const EXCLUDED_SITE = ["-site:msn.com","-site:youtube.com", "-site:amazon.com"]
const FOCUSED_SITE = ["site:cbc.ca", "site:cnn.com"]
var id = 1 //keep track of article ids in the db
// const client = server.client
//ChatGPT usage: No
async function searchNews(query){
    var url = bing_endpoints+"/search"
    var user_query;
    if (query === ""){
        user_query = EXCLUDED_SITE.join(" ") + " " + FOCUSED_SITE.join(" OR ")
        // user_query += " " + FOCUSED_SITE.join(" OR ")
    }
    else{
        user_query = query + " " + EXCLUDED_SITE.join(" ") + " " + FOCUSED_SITE.join(" OR ")
        // user_query +=" " + FOCUSED_SITE.join(" OR ")
    }
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
        // var articleBody = webContent
        if (articleBody.message && articleBody.stack){
            continue
        }
        else{
            articleEntry.title = article.name
            articleEntry.content = articleBody
        }
        
        articleEntry.publisher = article.provider[0].name.toLowerCase()
        articleEntry.publishedDate = article.datePublished

        var theme = article.category
        if (Array.isArray(theme)){
            articleEntry.categories = []
            console.log("An array") 
            for (var category of theme){
                console.log("117" + category)
                articleEntry.categories.push(category)
                addToTheme(category)
            }
        }
        else{
            if (theme == undefined){
                theme = query
            }
            
            articleEntry.categories = [theme]
            addToTheme(theme)
        }
        retrievedArticles.push(articleEntry)
    }
    addToDb(retrievedArticles)
    console.log(retrievedArticles)
    return retrievedArticles
}

function addToTheme(newTheme){
    for (var theme of server.forumTheme){
        if(theme.toLowerCase() === newTheme.toLowerCase()){
            return
        }
    }
    server.forumTheme.add(newTheme)
    server.forum.createForum(newTheme)
    console.log("new theme")
    return
}
// ChatGPT usage: No.
async function addToDb(articleList){
    for (var article of articleList){
        article.articleId = id
        id += 1
        await server.client.db("articledb").collection("articles").insertOne(article)
    }
}
bingNewsRetriever("")
export {bingNewsRetriever}
