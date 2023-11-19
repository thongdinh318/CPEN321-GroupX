import express from "express";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
import fs from "fs"
import https from "https"
import * as articleMod from "./articles/articlesMngt.js"
import { bingNewsRetriever } from "./articles/retriever.js";
import { collaborativeFilteringRecommendations } from "./articles/recommendation.js";
import { ForumModule } from "./forum_module/forum_interface.js";
import WebSocket, {WebSocketServer} from "ws";

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

const server = https.createServer(options, app);
const wss = new WebSocketServer({ port: 9000 });


const forum = new ForumModule()
const RETRIEVE_INTERVAL = 4.32 * Math.pow(10,7) //12 hours
var retriever = null //place holder for the retriever before init server

// Error checking function
//https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
// ChatGPT usage: No.
function isErr(error){
    return error, error.e, error.stack
}


//USER MODULE --->

//Verify and register users
// ChatGPT usage: No.
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
            res.status(400).send(rejectMsg)
        })
        
    } catch (error) {
        res.status(400).send("Verification Error")
    }
})


//Get a user profile
// ChatGPT usage: No.
app.get("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    var user = await userMod.getProfile(userId)
    if (isErr(user)){ 
        res.status(400).send("Error when getting user profile")
    }
    else{
        if (user.userId == undefined){
            res.status(400).send("User Profile not Found")
        }
        else{
            res.status(200).send(user)
        }
    }
})


//Get a user list of subscriptions
// ChatGPT usage: No.
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


//Get reading history
// ChatGPT usage: No.
app.get("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId

    var userProfile = await userMod.getProfile(userId);
    if(isErr(userProfile)){
        res.status(400).send("Error when getting reading history")
    }
    else{
        if (userProfile.userId){
            
            var articleArray = []
		console.log(userProfile.history)
            for(var article of userProfile.history){
                var foundArticle = await articleMod.searchById(article.articleId);
                articleArray.push(foundArticle)
            }
            res.status(200).send(articleArray)
        }
        else{
            res.status(200).send([])
        }
    }
})


//Update profile of a user, including the subscription list
// ChatGPT usage: No.
app.put("/profile/:userId", async (req,res)=>{
    var userId = req.params.userId
    const newProfile = req.body
    var succeed = await userMod.updateProfile(userId, newProfile)
    if (isErr(succeed)){
        res.status(400).send("Error when updating user profile")
    }
    else{
        if (!succeed){
            res.status(400).send("Cannot Update Profile/User not found")
        }
        else{
	    res.status(200).send("Profile was updated")
        }
    }
})


//Add a new article to reading history of a user
// ChatGPT usage: No.
app.put("/profile/:userId/history", async (req,res)=>{
    var userId = req.params.userId
    const newViewed = req.body
	console.log(newViewed)
	console.log(userId)
    var succeed = await userMod.updateHistory(userId, newViewed);
    if(isErr(succeed)){
        res.status(400).send("Error when updating reading history")
    }
    else{
        if (!succeed){
            res.status(400).send("Cannot Update History/User not found")
        }
        else{
            res.status(200).send("Article added to history")
        }
    }
})
//<--- USER MODULE

//ARTICLE MODULE --->

//Get article by id
// ChatGPT usage: No.
app.get("/article/:articleId", async (req,res)=>{
    var articleId = parseInt(req.params.articleId,10);
    console.log(articleId)
    var foundArticle = await articleMod.searchById(articleId);

    if(isErr(foundArticle)){
        res.status(400).send("Error when Searching by id")
    }
    else{
        if (foundArticle.articleId == undefined){
            res.status(400).send("Article Id Not Found")
        }
        else{
            res.status(200).send(foundArticle)
        }
    }
})


// Search using filters
// ChatGPT usage: No.
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
	console.log(foundArticles)
    if(isErr(foundArticles)){
        res.status(400).send("Error when Searching by filter")
    }
    else{
        if (foundArticles.length == 0){
            res.status(400).send("No articles matched")
        }
        else{
            res.status(200).send(foundArticles)
        }
    }
})


// Search keyword in articles
// ChatGPT usage: No.
app.get("/article/kwsearch/search", async(req,res)=>{
    var keyWord = req.query.keyWord
    console.log(keyWord)

    var query = {$or: [{content: {$regex: keyWord, $options:"i"}}, {title: {$regex: keyWord, $options:"i"}}]}
    var foundArticles = await articleMod.searchByFilter(query);

    if(isErr(foundArticles)){
        res.status(400).send("Error when searching with search bar")
    }
    else{
        if (foundArticles.length == 0){
            res.status(400).send("No articles matched")
        }
        else{
            res.status(200).send(foundArticles)
        }
    }
})
//<--- ARTICLE MODULE

//FORUM MODULE --->

// Get all forums
// ChatGPT usage: No.
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


// GET one specific forum, queried with forum id
// ChatGPT usage: No.
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


// Post a comment to a forum
// ChatGPT usage: No.
// app.post("/addComment/:forum_id",async (req, res)=>{
// 	try{
// 		let commentData = req.body.commentData;
// 		let userId = req.body.userId
// 		const user = await userMod.getProfile(userId)
		
// 		const result = await forum.addCommentToForum(parseInt(req.params.forum_id,10), commentData, user.username)
// 		// await client.db('ForumDB').collection('forums').updateOne({ id : req.params.forum_id}, { $push:{ comments : comment }});

// 		if (isErr(result)){
// 			res.status(400).send("Could not post comment")
// 		}
// 		else{
// 			if (result){
// 				// make a get request to get the updated forum
//                 		const updatedForum =await forum.getForum(parseInt(req.params.forum_id),10);
// 				res.status(200).send(updatedForum);
// 				//res.status(200).send("Comment Posted!");
// 			}
// 			else{
// 				res.status(400).send("Failed Posting Comment! Please Try Again")
// 			}
// 		}

// 	}catch (err){
// 		res.status(400).send(err);
// 	}
// } );


wss.on('connection', async (ws) => {
    console.log('A new client Connected!');
  
    ws.on('message', async (comment, isBinary) =>{
  
      comment = JSON.parse(comment);
  
      // console.log(comment);
  
      let commentData = comment.content;
      let userId = comment.userId;
      const user = await userMod.getProfile(userId);
      let forum_id = comment.forum_id;
      let parent_id = comment.comment_id;
      
      const res = await forum.addCommentToForum(forum_id, commentData, user.username, parent_id).then()
  
      // if res is err
      if(!res) {
  
          ws.send("Could not post comment")
      }else{
          try{
              const newForum = await forum.getForum(forum_id);
              // console.log(newForum)
              // ws.send(newForum, {binary : isBinary});
              wss.clients.forEach(async (socketClient)=>{
                // If the new comment does not appear on the user's screen
                // try removing socketClient !== ws
                  if (socketClient !== ws && socketClient.readyState === WebSocket.OPEN){
                      socketClient.send(newForum);
                  }
              });
              
          }catch{
              ws.send("Could not post comment")
          }
      }
      
    });
    
  });
  





// <--- FORUM MODULE

//Recommedation module --->

//Get recommended list of articles for a user
// ChatGPT usage: No.
app.get("/recommend/article/:userId", async (req,res)=>{
    var userId = req.params.userId;
    try {
        const recommeded = await collaborativeFilteringRecommendations(userId);
        // console.log(recommeded)
        var recommededArticles = []
        for (var i = 0; i < recommeded.length; ++i){
            var articleId = recommeded[i][0]
            var article = await articleMod.searchById(parseInt(articleId,10))
            recommededArticles.push(article)
        }
        res.status(200).send(recommededArticles)
        
    } catch (error) {
	    console.log(error)
        res.status(400).send("Error when recommending articles")
        
    }
})

// ChatGPT usage: No.
app.get("/recommend/publisher/:userId", async (req,res)=>{
    var userId = req.params.userId;
    try {
        const recommeded = await collaborativeFilteringRecommendations(userId);
        var recommededPublishers = []
        for (var i = 0; i < recommeded.length; ++i){
            var articleId = recommeded[i][0]
            var article = await articleMod.searchById(parseInt(articleId,10))
            recommededPublishers.push(article.publisher)
        }
        res.status(200).send(recommededPublishers)
        
    } catch (error) {
	    console.log(error)
        res.status(400).send("Error when recommending publishers")
        
    }
})
// <--- Recommendation module



// Main Function
// ChatGPT usage: No.
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
        server.listen(8081)

        client.db("userdb").collection("profile").deleteMany({})
        
	    client.db("articledb").collection("articles").deleteMany({}) //when testing, run the server once then comment out this line so the article db does not get cleaned up on startup
        
	    client.db("ForumDB").collection("forums").deleteMany({})
	
	    await userMod.initUDb()
        
	    await articleMod.initADb() // when testing, run the server once the comment out this line so we don't overcrowded the db with root article

        await forum.createForum(forum_id++,"General News")
        await forum.createForum(forum_id++, "Economics")
        await forum.createForum(forum_id++, "Education")
        console.log("Retrieving some articles")
        
	    await bingNewsRetriever("") //when testing, run the server once then comment out this line so we don't make unnecessary transactions to the api
        
	    console.log("Server is ready to use")
        retriever = setInterval(bingNewsRetriever, RETRIEVE_INTERVAL, "") //get general news every 1 minutes

    } catch (error) {
        console.log(error)

        if (retriever != null){
            clearInterval(retriever)
        }
        await client.close()
    }
}
run()
