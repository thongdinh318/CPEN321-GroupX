import * as server from "../server.js"
// const client = server.client

// const defArticle = {
//     "articleId": 0,
//     "views": 0,
//     "content": "",
//     "categories":[],
//     "publisher":"",
//     "publishedDate": null,
// }


//Intit DB Function-->
// ChatGPT usage: No.
/*export async function initADb(){
    await server.client.db("articledb").collection("articles").insertOne(defArticle)
    return("success\n")
}*/
//<--- Intit DB Function

//Search an article based on its id 
// ChatGPT usage: No.
export async function searchById(articleId){
    var foundArticle  =  server.client.db("articledb").collection("articles").find({articleId});
    foundArticle = await foundArticle.toArray()
    // console.log(foundArticle)
    if (foundArticle == undefined || foundArticle.length === 0){
        return ({})
    }
    return foundArticle[0]
}

//Search the database for a list of articles that match the query provided by the user
// ChatGPT usage: No.
export async function searchByFilter(query){

    var foundArticles  = server.client.db("articledb").collection("articles").find(query);
    foundArticles = await foundArticles.toArray()
    
    // console.log(foundArticles)
    if (foundArticles === undefined || foundArticles.length == 0){
        return []
    }
    
    if (foundArticles.length > 10){
        foundArticles = foundArticles.slice(0,9)
    }
    return foundArticles

}

//Rerieve a list of all article ids in the database
//Used by the recommendation module
// ChatGPT usage: No.
export async function getArticleIds(){
    var articleCollection = await server.client.db("articledb").collection("articles").find({}).toArray()
    var articleIdList = [];

    articleCollection.forEach((article)=>{
        // if (article.articleId !== 0){
            articleIdList.push(article.articleId);
        // }
    })

    return (articleIdList)
}
// export {getArticleIds, searchByFilter,searchById, initADb}
