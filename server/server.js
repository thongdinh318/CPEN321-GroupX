import express from "express";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
import fs from "fs"
import https from "https"
import * as articleMod from "./articles/articlesMngt.js"
import { bingNewsRetriever } from "./articles/retriever.js";
import {ForumModule} from "./forum_module/forum_interface.js";
const uri = "mongodb://127.0.0.1:27017"
// const uri = "mongodb://20.220.32.123:27017"
const client = new MongoClient(uri)


var app = express()
const port = 8081;
const host = "127.0.0.1";
// const port = 320; 
// const host = "20.220.32.123";
app.use(express.json())



// Uncomment for https
// var options = {
// 	key: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/privkey.pem"),
// 	cert: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/fullchain.pem")
// };

const forum = new ForumModule()
const RETRIEVE_INTERVAL = 60000 //1 minutes
var retriever = null //place holder for the retriever before init server
// Error checking function
function isErr(error){
    //https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
    return error;
}

app.get("/", async (req,res)=>{
    res.status(200).send("Hellp From QuickNews")
})



//USER MODULE --->
//Get a user profile
app.get("/profile/:userId", async (req,res)=>{
    var userId = parseInt(req.params.userId);
    var result = await userMod.getProfile(userId)
    if (isErr(result)){ 
        res.status(400).send("Could not Find User");
    }
    else{
        res.status(200).send(result)
    }
})

// Add a new user
app.post("/addUser/:userId", async (req,res) =>{
    var userId = parseInt(req.params.userId);
    var profile = req.body;
    var result = await userMod.addUser(userId, profile);
    if (isErr(result)){ 
        
        res.status(400).send("Could not add user")
    }
    else{
        console.log("NEW user added!");
        res.status(200).send(result)
    }


});


//Get a user list of subscriptions
app.get("/profile/:userId/subscriptions", async (req,res)=>{
    var userId = parseInt(req.params.userId);
    var result = await userMod.getSubList(userId);
    if (isErr(result)){
        res.status(400).send("An unexpected error occured")
    }
    else{
        res.status(200).send(result)
    }
})


//Update profile of a user
app.put("/profile/:userId", async (req,res)=>{
    var userId = parseInt(req.params.userId)
    const newProfile = req.body
    var result = await userMod.updateProfile(userId, newProfile)
    if (isErr(result)){
        res.status(400).send("Could not update user profile")
    }
    else{
        res.status(200).send(result)
    }
})


// We already have update profile which updates everything
// if we want update history then we also need an "updateSubscriptions" function

//Update reading history of the user
// app.put("/profile/:userId/history", async (req,res)=>{
//     var userId = parseInt(req.params.userId)
//     const newHistory = req.body
//     var result = await userMod.updateHistory(userId, newHistory);
//     if(isErr(result)){
//         res.status(400).send(result)
//     }
//     else{
//         res.status(200).send(result)
//     }
// })
//<--- USER MODULE





//ARTICLE MODULE ---> 
//Get article by id
app.get("/article/:articleId", async (req,res)=>{

    var articleId = req.params.userId;
    console.log(articleId)
    var foundArticle = await articleMod.searchById(articleId);

    if(isErr(foundArticle)){
        res.status(400).send(foundArticle)
    }
    else{
        res.status(200).send(foundArticle)
    }
})

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
        console.log("ERROR")
        res.status(400).send(foundArticles)
    }
    else{
        res.status(200).send(foundArticles)
    }
})

// Search using search bar
app.get("/article/kwsearch/search", async(req,res)=>{
    var keyWord = req.query.keyWord

    var query = {content: {$regex: keyWord, $options:"i"}}
    var foundArticles = await articleMod.searchByFilter(query);

    if(isErr(foundArticles)){
        res.status(400).send(foundArticles)
    }
    else{
        res.status(200).send(foundArticles)
    }
})
//<--- ARTICLE MODULE



//FORUM MODULE --->
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

// GET one specific forum, queried with forum id
app.get("/forums/:forum_id", async (req, res) =>{
	try{
		
		const result =await forum.getForum(req.params.forum_id)
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
app.post("/addComment/:forum_id",async (req, res)=>{
	try{
		let commentData = req.body.commentData;
		let userId = req.body.userId
		const user = await getProfile(userId)
        console.log(user)
		
		const result = await forum.addCommentToForum(req.params.forum_id, commentData, user.username)
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

// Main Function
async function run(){
    try {
        
        console.log("NEW SERVER");
        await client.connect()
        console.log("Successfully connect to db");
	
        /* Use this for localhost test*/
	    // var server = app.listen(8081, (req,res)=>{
        //     var host = server.address().address
        //     var port = server.address().port
        //     console.log("Server is running at https://" + host +":" + port);
        // })

        app.listen(port,  () => {
            console.log(`${new Date()}  App Started. Listening on ${host}:${port}`);  
        });
	
        // create https server
        // https.createServer(options, app).listen(8081)
        await userMod.initUDb()
        await articleMod.initADb()
        // bingNewsRetriever("")
        // retriever = setInterval(bingNewsRetriever, RETRIEVE_INTERVAL, "") //get general news every 1 minutes

    } catch (error) {
        console.log(error)

        if (retriever!= null){
            clearInterval(retriever)
        }
        await client.close()
    }
}
await run()
