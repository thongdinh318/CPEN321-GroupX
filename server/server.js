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
    var result = await userMod.initDb(client, initNum)
    if (isErr(result)){
        res.status(400).send(result)
    }
    else{
        res.status(200).send(result)
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
    var articleId = splittedWords[2];
    var foundArticle = await artcileMod.searchById(client,articleId);

    if(isErr(foundArticle)){
        res.status(400).send(foundArticle)
    }
    else{
        res.status(200).send(foundArticle)
    }
})

// Search using filters
app.get("/article/search?publisher=:publisher&before=:beforeDate&after:afterDate&category:categories", async(req,res)=>{
    var filters = req.originalUrl.split("?")[1]
    filters = filters.split("&")

    var criteriaList = []

    filters.forEach(criteria => {
        var criterion = criteria.split("=")[1]
        criteriaList.push(criterion)
    });

    var publisher = criteriaList[0]
    var before = criteriaList[1]
    var after = criteriaList[2]

    var query = new Object()
    if (criteriaList[criteriaList.length-1] != ""){
        var categoryList = criteriaList[criteriaList.length-1].split(",")
        query.category = {$in: categoryList}
    }

    if (publisher != ""){
        query.publisher = {$text:{$search: publisher}}
    }
    if (before != "" && after != ""){
        before = new Date(before.toISOString())
        after = new Date(after.toISOString())
        query.publishedDate = {$gte:after, $lte: before}
    }
    else if (before != ""){
        before = new Date(before.toISOString())
        query.publishedDate = {$lte: before}
    }
    else if (after != ""){
        after = new Date(after.toISOString())
        query.publishedDate = {$gte: after}
    }

    var foundArticles = await artcileMod.searchByFilter(client, query)

    if(isErr(foundArticles)){
        res.status(400).send(foundArticles)
    }
    else{
        res.status(200).send(foundArticles)
    }
})

// Search using search bar
app.get("/article/search?keywords=:keywords", async(req,res)=>{
    var filters = req.originalUrl.split("?")[1]
    var keyWord = filters.split("=")[1]

    var query = {$text:{$search:keyWord}}

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