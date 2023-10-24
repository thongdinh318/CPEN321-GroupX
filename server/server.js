import express from "express";
// import {  initDb, getProfile, getSubList, updateProfile, updateHistory } from "./user.js";
import * as userMod from "./user.js"
import { MongoClient } from "mongodb";
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)
var app = express()

app.use(express.json())

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
        var server = app.listen(8081, (req,res)=>{
            var host = server.address().address
            var port = server.address().port
            console.log("Server is running at https://%s:%s",host,port)
        })
    } catch (error) {
        console.log(err)
        await client.close()
        
    }
}
run()