import express from "express";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
import fs from "fs"
import https from "https"
import * as artcileMod from "./articles/articlesMngt.js"
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)
var app = express()

app.use(express.json())

// var options = {
// 	key: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/privkey.pem"),
// 	cert: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/fullchain.pem")
// };
// Error checking function
function isErr(error){
    //https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
    return error, error.e, error.stack
}

//testing purpose -->
app.post("/initdb", async (req,res)=>{
    var initNum = req.body.initNum
    var result1 = await userMod.initDb(client, initNum)
    var result2 = await artcileMod.initADb(client, initNum)
    if (isErr(result1) || isErr(result2)){
        res.status(400).send("Failed to init dbs")
    }
    else{
        res.status(200).send("success/n")
    }
})
// <--- testing purpose

//--> User Module interfaces
app.get("/", async (req,res)=>{
    var result = await userMod.getAllUserHistory(client)
    res.status(200).send(result)
})

app.get("/profile/:userId", async (req,res)=>{
    var userId = parseInt(req.originalUrl.substring(9), 10)
    var result = await userMod.getProfile(client, userId)
    if (isErr(result)){ 
        res.status(400).send(result)
    }
    else{
        res.status(200).send(result)
    }
})

app.get("/profile/:userId/subscriptions", async (req,res)=>{
    var userId = req.originalUrl.substring(9)
    userId = parseInt(userId, 10)
    var result = await userMod.getSubList(client,userId);
    if (isErr(result)){
        res.status(400).send(result)
    }
    else{
        res.status(200).send(result)
    }
})

app.put("/profile/:userId", async (req,res)=>{
    var userId = req.originalUrl.substring(9)
    userId = parseInt(userId, 10)
    const newProfile = req.body
    var result = await userMod.updateProfile(client, userId, newProfile)
    if (isErr(result)){
        res.status(400).send(result)
    }
    else{
        res.status(200).send(result)
    }
})

app.put("/profile/:userId/history", async (req,res)=>{
    var userId = req.originalUrl.substring(9)
    userId = parseInt(userId, 10)
    const newHistory = req.body
    var result = await userMod.updateHistory(client, userId, newHistory);
    if(isErr(result)){
        res.status(400).send(result)
    }
    else{
        res.status(200).send(result)
    }
})
//<-- User Module interfaces

//--> Article Module interfaces
//Get article by id
app.get("/article/:articleId", async (req,res)=>{

    var splittedWords = req.originalUrl.split("/")
    var articleId = parseInt(splittedWords[2],10);
    console.log(articleId)
    var foundArticle = await artcileMod.searchById(client,articleId);

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
    var foundArticles = await artcileMod.searchByFilter(client, query)

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
    var foundArticles = await artcileMod.searchByFilter(client, query);

    if(isErr(foundArticles)){
        res.status(400).send(foundArticles)
    }
    else{
        res.status(200).send(foundArticles)
    }
})
async function run(){
    try {
        await client.connect()
        console.log("Successfully connect to db")
	
        /* Use this for localhost test*/
	 var server = app.listen(8081, (req,res)=>{
            var host = server.address().address
            var port = server.address().port
            console.log("Server is running at https://%s:%s",host,port)
        })
	
	// create https server
	// https.createServer(options, app).listen(8081)
    } catch (error) {
        console.log(err)
        await client.close()
        
    }
}
run()