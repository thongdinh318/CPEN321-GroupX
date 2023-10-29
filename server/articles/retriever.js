'use strict'
import axios from 'axios'
import cheerio from 'cheerio'

const bing_endpoints = "https://api.bing.microsoft.com/v7.0/news"
const key = '56e2a381b452440abe22ce6ffb33b485'
const EXCLUDED_SITE = ["-site:msn.com","-site:youtube.com", "-site:amazon.com"]
async function searchNews(query){
    var url = bing_endpoints+"/search"
    console.log(url)
    try {
        var user_query = query + " " + EXCLUDED_SITE.join(" ")
        console.log(user_query)
        const res = await axios.get(url, {
            headers:{ 'Ocp-Apim-Subscription-Key': key},
            params:{
                q:user_query,
                count: 3,
                sortBy:"Date",
                freshness:"Month",
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

async function bingNewsRetriever(searchTerm){
    var result = await searchNews(searchTerm)
    
    var retrievedArticles =[]
    for (var article of result.value){
        var articleEntry = new Object()
        articleEntry.publisher = article.provider[0].name
        articleEntry.publishedDate = article.datePublished
        articleEntry.categories = article.category != undefined? [article.category]:[term]
        articleEntry.title = article.name
        articleEntry.url = article.url
        var content = await scrapeURL(article.url)
        articleEntry.content = content.para
    
        console.log(articleEntry)
        console.log("\n")
        retrievedArticles.push(articleEntry)
    }

    return retrievedArticles
}
export {bingNewsRetriever}