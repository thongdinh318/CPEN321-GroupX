import express from "express";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
import fs from "fs"
import https from "https"
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)
var app = express()

app.use(express.json())

var options = {
	key: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/privkey.pem"),
	cert: fs.readFileSync("/etc/letsencrypt/live/quicknews.canadacentral.cloudapp.azure.com/fullchain.pem")
};
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

app.get("/", (req,res)=>{
	res.status(200).send("Hello from HTTPS")
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
async function run(){
    try {
        await client.connect()
        console.log("Successfully connect to db")
	
        /* Use this for localhost test
	 * var server = app.listen(8081, (req,res)=>{
            var host = server.address().address
            var port = server.address().port
            console.log("Server is running at https://%s:%s",host,port)
        })*/
	
	// create https server
	https.createServer(options, app).listen(8081)
    } catch (error) {
        console.log(err)
        await client.close()
        
    }
}
run()
