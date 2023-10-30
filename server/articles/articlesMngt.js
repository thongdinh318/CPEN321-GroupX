const defArticle = {
    "articleId": null,
    "views": null,
    "content": "",
    "categories":[],
    "publisher":"",
    "publishedDate": null,
}

// Helper Functions --->
function createNewArticle(articleId){
    var newArticle = JSON.parse(JSON.stringify(defArticle))
    newArticle.articleId = articleId
    newArticle.views = 0
    if (articleId % 2 == 0){
        newArticle.content = "Summarized content of article"
        newArticle.publisher = "CBC"
        newArticle.categories = ["sport","criminal"]
        newArticle.publishedDate = new Date("2023-2-29").toISOString()
    }
    else{
        newArticle.content = "Something about CPEN 321 and News"
        newArticle.publisher = "CNN"
        newArticle.categories = ["education", "environment"]
        newArticle.publishedDate = new Date("2023-10-20").toISOString()
    }
    return newArticle
}

// <-- Helper Functions
//testing purpose -->
async function initADb(client, initNum){
    try {
        for (var id = 1; id < initNum; id++){
            var newArticle = createNewArticle(id)
            await client.db("articledb").collection("articles").insertOne(newArticle)
        }
        
        return("success\n")
    } catch (error) {
        return (error)
    }
}
//<-- testing purpose 
async function searchById(client, articleId){
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

async function searchByFilter(client, query){
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

async function getArticleIds(client){
    var articleCollection = await client.db("articledb").collection("articles").find({}).toArray()
    var articleIdList = [];

    articleCollection.forEach((article)=>{
        article.views = article.views + 1
        articleIdList.push(article.articleId);
    })

    return (articleIdList)
}
export {getArticleIds, searchByFilter,searchById, initADb}