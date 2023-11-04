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

//Intit DB Function-->
// ChatGPT usage: No.
async function initADb(){
    await client.db("articledb").collection("articles").insertOne(defArticle)
    return("success\n")
}
//<--- Intit DB Function
//Search an article based on its id 
// ChatGPT usage: No.
async function searchById(articleId){
    try {
        var foundArticle  = await client.db("articledb").collection("articles").find({articleId});
        foundArticle = await foundArticle.toArray()
        console.log(foundArticle)
        if (foundArticle == undefined || foundArticle.length == 0){
            return ({})
        }
        else{
            return foundArticle[0]
        }
    } catch (error) {
        return error
    }
}

//Search the database for a list of articles that match the query provided by the user
// ChatGPT usage: No.
async function searchByFilter(query){
    try {
        console.log(query)
        var foundArticles  = await client.db("articledb").collection("articles").find(query);
        foundArticles = await foundArticles.toArray()
        if (foundArticles.length > 10){
            foundArticles = foundArticles.slice(0,9)
        }
	
        if (foundArticles == undefined || foundArticles.length == 0){
            return []
        }
        else{
            return foundArticles
        }
    } catch (error) {
        return error
    }
}

//Rerieve a list of all article ids in the database
//Used by the recommendation module
// ChatGPT usage: No.
async function getArticleIds(){
    var articleCollection = await client.db("articledb").collection("articles").find({}).toArray()
    var articleIdList = [];

    articleCollection.forEach((article)=>{
        if (article.articleId != 0){
            articleIdList.push(article.articleId);
        }
    })

    return (articleIdList)
}
export {getArticleIds, searchByFilter,searchById, initADb}