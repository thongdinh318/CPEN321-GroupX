import express from "express";
import * as userMod from "./user.js"
import * as mongo from "mongodb";
import fs from "fs"
import https from "https"
import jwt from "jsonwebtoken";

import * as articleMod from "./articles/articlesMngt.js"
import * as retriever from "./articles/retriever.js";
import * as recommendation from "./articles/recommendation.js";
import ForumModule from "./forum_module/forum_interface.js";

const uri = "mongodb://127.0.0.1:27017"
export const client = new mongo.MongoClient(uri)

export var app = express()
app.use(express.json())
export const key = "super_secret_key"
var forum_id = 1;
export var forumTheme = new Set(["General News", "Economics", "Education"])
// Uncomment for https
// var options = {
//      key:fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/privkey.pem"),
//      cert:fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/fullchain.pem")
// };

const forum = new ForumModule()


// Error checking function
//https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
// ChatGPT usage: No.
function isErr(error){
    return (error.message && error.stack)
}


//USER MODULE --->

//Verify and register users
// ChatGPT usage: No.
app.post("/signin", async (req,res)=>{
    const token = req.body.idToken;
    const payloadPromise =  userMod.verify(token)
    payloadPromise.then((payload)=>{
        // console.log(payload)
        var loggedInUserPromise = userMod.registerNewUser(payload['sub'], payload['name'], payload['email'])
        loggedInUserPromise.then((loggedInUser)=>{
            // console.log(loggedInUser)
            delete loggedInUser.user._id
            res.status(200).send({user: loggedInUser.user, jwt: loggedInUser.jwt})
        })
    }).catch((rejectError)=>{
        // console.log(rejectError.message)
        res.status(400).send(rejectError.message)
    })
})

app.use("/signout", (req,res,next)=>{
    // console.log(req.headers)
    if (req.headers.jwt == undefined){
        res.status(400).send("No JWT in headers")
    }
    try {
        var decoded = jwt.verify(req.headers.jwt, key)
    } catch (err) {
        res.status(403).send(err.message)
        return
    }
    if (decoded.id === req.body.userId){
        // console.log("Rigth token, proceed")
        next()
    }
    else{
        res.status(400).send("Wrong token")
        return;
    }
})
app.put("/signout", async(req, res)=>{
    var userId = req.body.userId
    var jwtFound = await client.db("tokendb").collection("jwt").findOne({userId})
    if (jwtFound){
        client.db("tokendb").collection("jwt").deleteOne({userId})
        res.status(200).send("Signned out Success")
    }
    else{
        res.status(400).send("User already signed out")
    }
})


app.use("/profile/:userId", (req,res,next)=>{
    // console.log(req.headers)
    if (req.headers.jwt == undefined){
        res.status(400).send("No JWT in headers")
    }
    try {
        var decoded = jwt.verify(req.headers.jwt, key)
    } catch (err) {
        res.status(403).send(err.message)
        return
    }
    if (decoded.id === req.params.userId){
        // console.log("Rigth token, proceed")
        next()
    }
    else{
        res.status(400).send("Wrong token")
        return;
    }
})
//Get a user profile
// ChatGPT usage: No.
app.get("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    var user = await userMod.getProfile(userId)
    // if (isErr(user)){ 
    //     res.status(400).send("Error when getting user profile")
    // }
    // else{
    // }
    if (user.userId == undefined){
        res.status(400).send("User Profile not Found")
    }
    else{
        delete user._id
        res.status(200).send(user)
    }
})


//Get a user list of subscriptions
// ChatGPT usage: No.
app.get("/profile/:userId/subscriptions", async (req,res)=>{
    var userId = req.params.userId
    var userProfile = await userMod.getProfile(userId);
    // if (isErr(userProfile)){
    //     res.status(400).send("Error when getting subscription list")
    // }
    // else{
    // }
    if (userProfile.userId){
        res.status(200).send(userProfile.subscriptionList)
    }
    else{
        res.status(400).send([])
    }
})


//Get reading history
// ChatGPT usage: No.
app.get("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId

    var userProfile = await userMod.getProfile(userId);
    // if(isErr(userProfile)){
    //     res.status(400).send("Error when getting reading history")
    // }
    // else{
    // }
    if (userProfile.userId == undefined){
        res.status(400).send([])
    }
    else{
        var articleArray = []
        // console.log(userProfile.history)
        for(var article of userProfile.history){
            var foundArticle = await articleMod.searchById(article.articleId);
            articleArray.push(foundArticle)
        }
        res.status(200).send(articleArray)
    }
})


//Update profile of a user, including the subscription list
// ChatGPT usage: No.
app.put("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    const newProfile = req.body
    var succeed = await userMod.updateProfile(userId, newProfile)
    // if (isErr(succeed)){
    //     res.status(400).send("Error when updating user profile")
    // }
    // else{
    // }
    if (!succeed){
        res.status(400).send("Cannot Update Profile/User not found")
    }
    else{
        res.status(200).send("Profile was updated")
    }
})


//Add a new article to reading history of a user
// ChatGPT usage: No.
app.put("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId
    const newViewed = req.body
	// console.log(newViewed)
	// console.log(userId)
    var succeed = await userMod.updateHistory(userId, newViewed);
    // if(isErr(succeed)){
    //     res.status(400).send("Error when updating reading history")
    // }
    // else{
    // }
    if (!succeed){
        res.status(400).send("Cannot Update History/User not found")
    }
    else{
        res.status(200).send("Article added to history")
    }
})
//<--- USER MODULE

//ARTICLE MODULE --->

//Get article by id
// ChatGPT usage: No.
app.get("/article/:articleId", async (req,res)=>{
    var articleId = parseInt(req.params.articleId,10);
    // console.log(articleId)
    var foundArticle = await articleMod.searchById(articleId);

    // if(isErr(foundArticle)){
    //     res.status(400).send("Error when Searching by id")
    // }
    // else{
    // }
    if (foundArticle.articleId == undefined){
        res.status(400).send("Article Id Not Found")
    }
    else{
        res.status(200).send(foundArticle)
    }
})

function sanitiezInputs(input){
    Object.keys(input.query).forEach((key) => {
        input.query[key] = input.query[key].replaceAll('<', '');
        input.query[key] = input.query[key].replaceAll('>', '');
        input.query[key] = input.query[key].replaceAll('\'', '');
        input.query[key] = input.query[key].replaceAll('\"', '');
        input.query[key] = input.query[key].replaceAll('$', '');
      });
}
app.use("/article/filter/search", (req,res,next)=>{
    sanitiezInputs(req)
    next()
})
// Search using filters
// ChatGPT usage: No.
app.get("/article/filter/search", async(req,res)=>{
    var publisher = req.query.publisher
    var end = req.query.before
    var start = req.query.after
    var categories = req.query.categories
    var keyWord = req.query.kw

    if (publisher == undefined || end == undefined || start == undefined || keyWord == undefined){
        res.status(400).send("Invalid query. Please try again")
        return;
    }

    var query = {}
    if (end != "" && start != ""){
        if (end < start){
            res.status(400).send("Invalid date range. Please try again")
            return;
        }

        if (end == start){
            end = new Date(new Date(end).setUTCHours(23,59,59,999)).toISOString()
        }
        else{
            end = new Date(end).toISOString()
        }
        start = new Date(start).toISOString()
        query.publishedDate = {$gte:start, $lte: end}
    }
    else if (end != ""){
        end = new Date(end).toISOString()
        query.publishedDate = {$lte: end}
        
    }
    else if (start != ""){
        start = new Date(start).toISOString()
        query.publishedDate = {$gte: start}
    }


    if (keyWord != ""){
        query.$or = [{content: {$regex: keyWord, $options:"i"}}, {title: {$regex: keyWord, $options:"i"}}]
    }
    if (publisher != ""){
        query.publisher = {$regex: publisher, $options:"i"}
    }

    if (categories != ""){
        var list = categories.split(",")
        query.categories = {$in: list}
    }
    console.log(query)
    var foundArticles = await articleMod.searchByFilter(query)
    // if(isErr(foundArticles)){
    //     res.status(400).send("Error when Searching by filter")
    // }
    // else{
    // }
    if (foundArticles.length == 0){
        res.status(400).send("No articles matched")
    }
    else{
        res.status(200).send(foundArticles)
    }
})


// Search keyword in articles
// ChatGPT usage: No.
app.get("/article/kwsearch/search", async(req,res)=>{
    var keyWord = req.query.keyWord
    // console.log(keyWord)

    var query = {$or: [{content: {$regex: keyWord, $options:"i"}}, {title: {$regex: keyWord, $options:"i"}}]}
    var foundArticles = await articleMod.searchByFilter(query);

    // if(isErr(foundArticles)){
    //     res.status(400).send("Error when searching with search bar")
    // }
    // else{
    // }
    if (foundArticles.length === 0){
        res.status(400).send("No articles matched")
    }
    else{
        res.status(200).send(foundArticles)
    }
})

app.use("/article/subscribed/:userId", (req,res,next)=>{
    if (req.headers.jwt == undefined){
        res.status(400).send("No JWT in headers")
        return
    }
    try {
        var decoded = jwt.verify(req.headers.jwt, key)
    } catch (err) {
        res.status(403).send(err.message)
        return
    }
    if (decoded.id === req.params.userId){
        // console.log("Rigth token, proceed")
        next()
    }
    else{
        res.status(400).send("Wrong token")
        return;
    }
})
//ChatGPT Ussage: No
app.get("/article/subscribed/:userId", async (req,res)=>{
    const userId = req.params.userId
    const userProfile = await userMod.getProfile(userId)
    if (userProfile.userId == undefined){
        res.status(400).send("User not found")
        return
    }
    // else{
    // }
    const userSubList = userProfile.subscriptionList
    var query = new Object()
    if (userSubList.length != 0){
        for (var i = 0; i < userSubList.length; i++){
            userSubList[i] = new RegExp(userSubList[i].toLowerCase())
        }
        query.publisher =  {$in:userSubList}
    }
    
    const foundArticles = await articleMod.searchByFilter(query)
    if (foundArticles.length == 0){
        res.status(400).send("No articles found")
    }
    else{
        res.status(200).send(foundArticles)
    }
})
//<--- ARTICLE MODULE

//FORUM MODULE --->

// Get all forums
// ChatGPT usage: No.
app.get("/forums", async (req, res) =>{
    const result = await forum.getAllForums();
    // if (isErr(result)){
    //     res.status(400).send("Cannot get forum list")
    // }
    // else{
    // }
    res.status(200).send(result);
}); 


// GET one specific forum, queried with forum id
// ChatGPT usage: No.
app.get("/forums/:forum_id", async (req, res) =>{
    const result =await forum.getForum(parseInt(req.params.forum_id),10)
    // const result = await client.db("ForumDB").collection("forums").find({id : req.params.forum_id}).toArray();
    if (isErr(result) || result.length === 0){
        res.status(400).send("Forum does not exist")
    }
    else{
        res.status(200).send(result);
    }
});  


app.use("/addComment/:forum_id", (req,res,next)=>{
    if (req.headers.jwt == undefined){
        res.status(400).send("No JWT in headers")
        return
    }
    try {
        var decoded = jwt.verify(req.headers.jwt, key)
    } catch (err) {
        res.status(403).send(err.message)
        return
    }
    if (decoded.id === req.body.userId){
        // console.log("Rigth token, proceed")
        next()
    }
    else{
        res.status(400).send("Wrong token")
        return;
    }
})
// Post a comment to a forum
// ChatGPT usage: No.
app.post("/addComment/:forum_id",async (req, res)=>{
    let commentData = req.body.commentData;
    let userId = req.body.userId
    const user = await userMod.getProfile(userId)
    
    if(user.username == null){
        res.status(500).send("Could not post comment: Invalid UserId");
        return;
    }

    const result = await forum.addCommentToForum(parseInt(req.params.forum_id,10), commentData, user.username)

    // if (isErr(result)){
    //     res.status(500).send("Could not post comment")
    // }
    // else{
    // }
    if (result){
        // make a get request to get the updated forum
        const updatedForum =await forum.getForum(parseInt(req.params.forum_id),10);
        res.status(200).send(updatedForum);
    }
    else{
        res.status(500).send("Could not post comment")
    }
} );

// <--- FORUM MODULE

//Recommedation module --->

app.use("/recommend/article/:userId", (req,res,next)=>{
    if (req.headers.jwt == undefined){
        res.status(400).send("No JWT in headers")
    }
    try {
        var decoded = jwt.verify(req.headers.jwt, key)
    } catch (err) {
        res.status(403).send(err.message)
        return
    }
    if (decoded.id === req.params.userId){
        next()
    }
    else{
        res.status(400).send("Wrong token")
        return;
    }
})

function sortRecommended(ratingArr, articleArr){
    var result = []
    for (var i = 0; i < ratingArr.length; i++){
        var mostRecentArticle = articleArr[i]
        var mostRecentArticleIndex = i
        for (var j = i; j < ratingArr.length; j++){
            if (ratingArr[j][1] == ratingArr[i][1]){
                if (articleArr[j].publishedDate > articleArr[i].publishedDate){
                    mostRecentArticle = articleArr[j]
                    mostRecentArticleIndex = j
                }
            }
            if (ratingArr[j][1] > ratingArr[i][1]){
                break;
            }
        }
        result.push(mostRecentArticle)
        if (mostRecentArticleIndex != i){
            // Swap in ratingArr
            var temp = ratingArr[mostRecentArticleIndex]
            ratingArr[mostRecentArticleIndex] = ratingArr[i]
            ratingArr[i] = temp
    
            //swap in articleArr
            temp = mostRecentArticle
            articleArr[mostRecentArticleIndex] = articleArr[i]
            articleArr[i] = temp
        }
    }
    console.log(result)
    return result
}
//Get recommended list of articles for a user
// ChatGPT usage: No.
app.get("/recommend/article/:userId", async (req,res)=>{
    var userId = req.params.userId;
    // try {
        const userProfile = await userMod.getProfile(userId)
        if (userProfile.userId == undefined){
            res.status(400).send("User not Found")
            return
        }
        var ratings = await recommendation.collaborativeFilteringRecommendations(userId);
        console.log(ratings)
        var recommededArticles = []
        for (var i = 0; i < ratings.length; ++i){
            var articleId = ratings[i][0]
            var article = await articleMod.searchById(parseInt(articleId,10))
            recommededArticles.push(article)
        }

        var result = sortRecommended(ratings, recommededArticles)
        res.status(200).send(result)
        
    // } catch (error) {
    //     console.log(error)
    //     res.status(400).send("Error when recommending articles")
    // }
})

// <--- Recommendation module


// Main Function
// ChatGPT usage: No.
export var server = app.listen(8081, (req,res)=>{
    var host = server.address().address
    var port = server.address().port
})

// create https server
// export var server = https.createServer(options, app).listen(8081)
async function run(){
    const RETRIEVE_INTERVAL = 4.32 * Math.pow(10,7) //12 hours
    try {
        await client.connect()
        console.log("Successfully connect to db")
        /* Use this for localhost test*/

        client.db("userdb").collection("profile").deleteMany({})
        client.db("articledb").collection("articles").deleteMany({}) //when testing, run the server once then comment out this line so the article db does not get cleaned up on startup
        client.db("ForumDB").collection("forums").deleteMany({})
        client.db("tokendb").collection("jwt").deleteMany({})
	
        await userMod.initUDb()
        await articleMod.initADb() // when testing, run the server once the comment out this line so we don't overcrowded the db with root article

        for (var theme of forumTheme){
            await forum.createForum(forum_id++,theme)
        }
        // console.log("Retrieving some articles")
        // await retriever.bingNewsRetriever("") //when testing, run the server once then comment out this line so we don't make unnecessary transactions to the api
        // var retrieverInterval = setInterval(retriever.bingNewsRetriever, RETRIEVE_INTERVAL, "") //get general news every 1 min
        console.log("Server is ready to use")
    } catch (error) {
        // if (retrieverInterval != null){
        //      clearInterval(retrieverInterval)
        // }
        await client.close()
        server.close()
    }
}
run()
