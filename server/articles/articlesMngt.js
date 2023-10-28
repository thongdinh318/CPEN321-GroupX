const defArticle = {
    "articleId": null,
    "views": null,
    "content": "",
    "categories":[],
    "publisher":"",
    "publishedDate": null,
}

async function searchById(client, articleId){
    try {
        var foundArticle  = await client.db("articledb").collection("articles").find({"articleId": articleId});
        foundArticle = await result.toArray()
        if (foundArticle == undefined || foundArticle.length == 0){
            return null
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
        var foundArticles  = await client.db("articledb").collection("articles").find(query);
        foundArticles = await result.toArray()
        if (foundArticles == undefined || foundArticles.length == 0){
            return null
        }
        else{
            return foundArticles
        }
    } catch (error) {
        console.log(error)
        return error
    }
}

export {searchByFilter,searchById}