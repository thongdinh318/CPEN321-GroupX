import { MongoClient } from "mongodb";

const defArticle = {
    "articleId": 0,
    "views": 0,
    "content": "",
    "categories":[],
    "publisher":"",
    "publishedDate": null,
}

const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

// Helper Functions --->
// function createNewArticle(articleId){
//     var newArticle = JSON.parse(JSON.stringify(defArticle))
//     newArticle.articleId = articleId
//     newArticle.views = 0
//     if (articleId % 2 == 0){
//         newArticle.content = "Summarized content of article"
//         newArticle.publisher = "CBC"
//         newArticle.categories = ["sport","criminal"]
//         newArticle.publishedDate = new Date("2023-2-29").toISOString()
//     }
//     else{
//         newArticle.content = "Something about CPEN 321 and News"
//         newArticle.publisher = "CNN"
//         newArticle.categories = ["education", "environment"]
//         newArticle.publishedDate = new Date("2023-10-20").toISOString()
//     }
//     return newArticle
// }
// <-- Helper Functions

//Intit DB Function-->
async function initADb(){
    try {
        await client.db("articledb").collection("articles").insertOne(defArticle)
        return("success\n")
    } catch (error) {
        return (error)
    }
}
//<--- Intit DB Function
async function searchById(articleId){
    try {
        var foundArticle  = await client.db("articledb").collection("articles").find({"articleId": articleId});
        foundArticle = await foundArticle.toArray()
        console.log(foundArticle)
        if (foundArticle == undefined || foundArticle.length == 0){
            return ("Not Found")
        }
        else{
            return foundArticle[0]
        }
    } catch (error) {
        console.log(error)
        return error
    }
}

async function searchByFilter(query){
    try {
        // console.log(query)
        var foundArticles  = await client.db("articledb").collection("articles").find(query);
        foundArticles = await foundArticles.toArray()
        console.log(foundArticles)
        if (foundArticles == undefined || foundArticles.length == 0){
            return "Not F"
        }
        else{
            return foundArticles
        }
    } catch (error) {
        // console.log(error)
        return error
    }
}

async function getArticleIds(){
    var articleCollection = await client.db("articledb").collection("articles").find({}).toArray()
    var articleIdList = [];

    articleCollection.forEach((article)=>{
        if (article.articleId != 0){
            article.views = article.views + 1
            articleIdList.push(article.articleId);
        }
    })

    return (articleIdList)
}
export {getArticleIds, searchByFilter,searchById, initADb}