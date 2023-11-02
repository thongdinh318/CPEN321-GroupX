import express from "express";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
import fs from "fs"
import https from "https"
import * as articleMod from "./articles/articlesMngt.js"
import { bingNewsRetriever } from "./articles/retriever.js";
import { collaborativeFilteringRecommendations } from "./articles/recommendation.js";
import { ForumModule } from "./forum_module/forum_interface.js";
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

var app = express()
app.use(express.json())

var forum_id = 1;
// const id_token = "" // for test

// Uncomment for https
var options = {
 	key: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/privkey.pem"),
 	cert: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/fullchain.pem")
};

const forum = new ForumModule()
const RETRIEVE_INTERVAL = 60000 //1 minutes
var retriever = null //place holder for the retriever before init server

// Error checking function
//https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
function isErr(error){
    return error, error.e, error.stack
}


//USER MODULE --->
// ChatGPT usage: No.
//Verify and register users
app.post("/signin", async (req,res)=>{
    try {
        const token = req.body.idToken;
        const payloadPromise =  userMod.verify(token)
        payloadPromise.then((payload)=>{
            console.log(payload)
            var loggedInUserPromise = userMod.registerNewUser(payload['sub'], payload['name'], payload['email'])
            loggedInUserPromise.then((loggedInUser)=>{
                console.log(loggedInUser)
                res.status(200).send(loggedInUser)
            })
        }).catch((rejectMsg)=>{
            res.status(200).send(rejectMsg)
        })
        
    } catch (error) {
        res.status(400).send("Verification Error")
    }
})

// ChatGPT usage: No.
//Get a user profile
app.get("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    var user = await userMod.getProfile(userId)
    if (isErr(user)){ 
        res.status(400).send("Error when getting user profile")
    }
    else{
        if (user.userId == undefined){
            res.status(200).send("User Profile not Found")
        }
        else{
            res.status(200).send(user)
        }
    }
})

// ChatGPT usage: No.
//Get a user list of subscriptions
app.get("/profile/:userId/subscriptions", async (req,res)=>{
    var userId = req.params.userId
    var userProfile = await userMod.getProfile(userId);
    if (isErr(userProfile)){
        res.status(400).send("Error when getting subscription list")
    }
    else{
        if (userProfile.userId){
            res.status(200).send(userProfile.subscriptionList)
        }
        else{
            res.status(200).send([])
        }
    }
})

// ChatGPT usage: No.
//Get reading history
app.get("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId

    var userProfile = userMod.getProfile(userId);

    if(isErr(userProfile)){
        res.status(400).send("Error when getting reading history")
    }
    else{
        if (userProfile.userId){
            res.status(200).send(userProfile.history)
        }
        else{
            res.status(200).send([])
        }
    }
})

// ChatGPT usage: No.
//Update profile of a user, including the subscription list
app.put("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    const newProfile = req.body
    var succeed = await userMod.updateProfile(userId, newProfile)
    if (isErr(succeed)){
        res.status(400).send("Error when updating user profile")
    }
    else{
        if (!succeed){
            res.status(200).send("Cannot Update Profile/User not found")
        }
        else{
            res.status(200).send(result)
        }
    }
})

// ChatGPT usage: No.
//Add a new article to reading history of a user
app.put("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId
    const newViewed = req.body
    var succeed = await userMod.updateHistory(userId, newViewed);
    if(isErr(succeed)){
        res.status(400).send("Error when updating reading history")
    }
    else{
        if (!succeed){
            res.status(200).send("Cannot Update History/User not found")
        }
        else{
            res.status(200).send(result)
        }
    }
})
//<--- USER MODULE

//ARTICLE MODULE --->

// ChatGPT usage: No.
//Get article by id
app.get("/article/:articleId", async (req,res)=>{
    var articleId = parseInt(req.params.articleId,10);
    console.log(articleId)
    var foundArticle = await articleMod.searchById(articleId);

    if(isErr(foundArticle)){
        res.status(400).send("Error when Searching by id")
    }
    else{
        if (foundArticle.articleId == undefined){
            res.status(200).send("Article Id Not Found")
        }
        else{
            res.status(200).send(foundArticle)
        }
    }
})

// ChatGPT usage: No.
// Search using filters
app.get("/article/filter/search", async(req,res)=>{
    var publisher = req.query.publisher
    var before = req.query.before
    var after = req.query.after
    var categories = req.query.categories

    var query = new Object()
    if (publisher != ""){
        query.publisher = {$regex: publisher, $options:"i"}
    }
    if (before != "" && after != ""){
        before = new Date(before).toISOString()
        after = new Date(after).toISOString()
        query.publishedDate = {$gte:after, $lte: before}
    }
    else if (before != ""){
        before = new Date(before).toISOString
        query.publishedDate = {$lte: before}
    }
    else if (after != ""){
        after = new Date(after).toISOString
        query.publishedDate = {$gte: after}
    }

    if (categories != ""){
        var list = categories.split(",")
        query.categories = {$in: list}
    }
    var foundArticles = await articleMod.searchByFilter(query)

    if(isErr(foundArticles)){
        res.status(400).send("Error when Searching by filter")
    }
    else{
        if (foundArticles.length == 0){
            res.status(200).send("No articles matched")
        }
        else{
            res.status(200).send(foundArticles)
        }
    }
})

// ChatGPT usage: No.
// Search keyword in articles
app.get("/article/kwsearch/search", async(req,res)=>{
    var keyWord = req.query.keyWord

    var query = {content: {$regex: keyWord, $options:"i"}}
    var foundArticles = await articleMod.searchByFilter(query);

    if(isErr(foundArticles)){
        res.status(400).send("Error when searching with search bar")
    }
    else{
        if (foundArticles.length == 0){
            res.status(200).send("No articles matched")
        }
        else{
            res.status(200).send(foundArticles)
        }
    }
})
//<--- ARTICLE MODULE

//FORUM MODULE --->
// ChatGPT usage: No.
// Get all forums
app.get("/forums", async (req, res) =>{
	try{
		const result = await forum.getAllForums();
		if (isErr(result)){
			res.status(400).send("Cannot get forum list")
		}
		else{
			res.status(200).send(result);
		}
	} catch (err){
		console.log(err);
		res.status(400).send("No Forums")
	}
}); 

// ChatGPT usage: No.
// GET one specific forum, queried with forum id
app.get("/forums/:forum_id", async (req, res) =>{
	try{
		const result =await forum.getForum(parseInt(req.params.forum_id),10)
		// const result = await client.db("ForumDB").collection("forums").find({id : req.params.forum_id}).toArray();
		if (isErr(result)){
			res.status(400).send(result)
		}
		else{
			res.status(200).send(result);
		}

	} catch (err){

		console.log(err);
		res.status(400).send("Forum was not found");

	}
});  

// ChatGPT usage: No.
// Post a comment to a forum
app.post("/addComment/:forum_id",async (req, res)=>{
	try{
		let commentData = req.body.commentData;
		let userId = req.body.userId
		const user = await userMod.getProfile(userId)
		
		const result = await forum.addCommentToForum(parseInt(req.params.forum_id,10), commentData, user.username)
		// await client.db('ForumDB').collection('forums').updateOne({ id : req.params.forum_id}, { $push:{ comments : comment }});

		if (isErr(result)){
			res.status(400).send("Could not post comment")
		}
		else{
			if (result){
				res.status(200).send("Comment Posted!");
			}
			else{
				res.status(200).send("Failed Posting Comment! Please Try Again")
			}
		}

	}catch (err){
		res.status(400).send(err);
	}
} );
// <--- FORUM MODULE

//Recommedation module --->
// ChatGPT usage: No.
//Get recommended list of articles for a user
app.get("/recommend/article/:userId", async (req,res)=>{
    var userId = req.params.userId;
    try {
        const recommeded = await collaborativeFilteringRecommendations(userId);
        var recommededArticles = []
        for (var i = 0; i < recommeded.length(); ++i){
            var articleId = recommeded[i][0]
            var article = await articleMod.getArticleIds(articleId)
            recommededArticles.push(article)
        }
        res.status(200).send(recommededArticles)
        
    } catch (error) {
        res.status(400).send("Error when recommending articles")
        
    }
})

// ChatGPT usage: No.
app.get("/recommend/publisher/:userId", async (req,res)=>{
    var userId = req.params.userId;
    try {
        const recommeded = await collaborativeFilteringRecommendations(userId);
        var recommededPublishers = []
        for (var i = 0; i < recommeded.length(); ++i){
            var articleId = recommeded[i][0]
            var article = await articleMod.getArticleIds(articleId)
            recommededPublishers.push(article.publisher)
        }
        res.status(200).send(recommededPublishers)
        
    } catch (error) {
        res.status(400).send("Error when recommending publishers")
        
    }
})
// <--- Recommendation module

// ChatGPT usage: No.
// Main Function
async function run(){
    try {
        await client.connect()
        console.log("Successfully connect to db")
        /* Use this for localhost test*/
	    // var server = app.listen(8081, (req,res)=>{
        //     var host = server.address().address
        //     var port = server.address().port
        //     console.log("Server is running at https://%s:%s",host,port)
        // })
	
        // create https server
        https.createServer(options, app).listen(8081)

        client.db("userdb").collection("profile").deleteMany({})
        client.db("articledb").collection("articles").deleteMany({})
        client.db("ForumDB").collection("forums").deleteMany({})
	await userMod.initUDb()
        await articleMod.initADb()

        await forum.createForum(forum_id++,"General News")
        await forum.createForum(forum_id++, "Economics")
        await forum.createForum(forum_id++, "Education")
        console.log("Retrieving some articles")
        await bingNewsRetriever("")
        console.log("Server is ready to use")
        retriever = setInterval(bingNewsRetriever, RETRIEVE_INTERVAL, "") //get general news every 1 minutes

    } catch (error) {
        console.log(error)

        if (retriever!= null){
            clearInterval(retriever)
        }
        await client.close()
    }
}
run()
