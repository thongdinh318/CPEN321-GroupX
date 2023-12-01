import * as OAuth2Client from 'google-auth-library'
import * as server from "./server.js"
const CLIENT_ID = "474807609573-3rub2rf78k2tirh75j9ivh9u16b7uor7.apps.googleusercontent.com"//TODO: replace witht the real client id
const ggClient = new OAuth2Client.OAuth2Client(CLIENT_ID);
import jwt from "jsonwebtoken";

//Default user setting
const defUser = { 
	"userId": '0',
    "username": "root",
	"dob": null,
	"email":null,
	"subscriptionList":[],
	"history":[]
}

// Helper Functions --->
//ChatGPT usage: No
async function checkAvailable(userId){
    var result = await server.client.db("userdb").collection("profile").findOne({userId})
    // var arr = await result.toArray()
    if (result == null){
        return({})
    }
    return result
}
//ChatGPT usage: No
function createNewUser(userId, userName, userEmail){
    var newUser = JSON.parse(JSON.stringify(defUser))
    newUser.userId = userId;
    newUser._id = userId;
    newUser.username = userName
    newUser.email = userEmail
    return newUser
}
// <-- Helper Functions

//Init DB function --->
//ChatGPT usage: No
/*async function initUDb(){
    await server.client.db("userdb").collection("profile").insertOne(defUser)
    return("success\n")
}*/
//<--- Init DB function

//Interfaces with frontend -->
//ChatGPT usage: No
function verify(token){
    return new Promise((resolve, reject)=>{
        ggClient.verifyIdToken(
            {idToken: token, audience: CLIENT_ID},
            function(err, login){
                if (err){
                    // console.log(err)
                    throw err
                }
                if (login){
                    const payload = login.getPayload()
                    resolve(payload)
                }
                else{
                    reject(new Error("invalid token"))
                }
            })
    })
}
//ChatGPT usage: No
async function registerNewUser(userId, username, userEmail){
    const userProfile = await checkAvailable(userId)

    if (userProfile.userId){
        await server.client.db("tokendb").collection("jwt").deleteOne({userId: userProfile.userId})
    
        var token = jwt.sign({id:userId,}, server.key, {algorithm:'HS256'}) // local
    
        // var token = jwt.sign({id:userId,}, server.key, {algorithm:'ES256'}) // cloud
    
        await server.client.db("tokendb").collection("jwt").insertOne({userId: userId, jwt: token})
        return ({user: userProfile, jwt: token})
    }
    
    var newUser = createNewUser(userId, username,userEmail)
    
    var token = jwt.sign({id:userId}, server.key, {algorithm:'HS256'}) // local
    // 
    // var token = jwt.sign({id:userId}, server.key, {algorithm:'ES256'}) // cloud
    
    await server.client.db("tokendb").collection("jwt").insertOne({userId: userId, jwt: token})
    await server.client.db("userdb").collection("profile").insertOne(newUser);
    return ({user: newUser, jwt: token})

}


// get profile
//ChatGPT usage: No
async function getProfile(userId){
    var user = await checkAvailable(userId)
    return user
}
//Update profile info
//ChatGPT usage: No
async function updateProfile(userId, newProfile){
    
    var user = await checkAvailable(userId)
    if (user.userId == undefined){
        return false
    }
    const result = await server.client.db("userdb").collection("profile").updateOne({userId}, {$set: newProfile})
    return (result.acknowledged)
}


//Update reading history
// ChatGPT usage: No.
async function updateHistory(userId, newViewed){
    var user = await checkAvailable(userId)
    // console.log(user)
    if (user.userId  == undefined){
        return false
    }
    var dup = false
    var updateHistory = user.history
    for (var pastViewed of updateHistory){
        if (pastViewed.articleId === newViewed.articleId){
            pastViewed.views = pastViewed.views + 1
            dup = true
            break;
        }
    }

    if (!dup){
        newViewed.views = 1 
        updateHistory.push(newViewed)
    }

    const result = await server.client.db("userdb").collection("profile").updateOne({userId}, {$set: {"history":updateHistory}})
    return (result.acknowledged)
}

// <--Interfaces with frontend 

// Interfaces with other modules -->


//Get the reading history of all users
//Used by Recommendation module
// ChatGPT usage: No.
async function getAllUserHistory(){
    var profileCollec = await server.client.db("userdb").collection("profile").find({}).toArray()
    
    var userItemData = [];
    var userList = [];

    profileCollec.forEach((user)=>{
        userList.push(user.userId);
        if (user.history.length > 0)
        {
            user.history.forEach((item)=>{
                var itemData = {}
                itemData.userId = user.userId;
                itemData.itemId = item.articleId;
                itemData.views = item.views;
                userItemData.push(itemData)
            })
        }
    })

    var result = {};
    result.users = userList;
    result.userItemData = userItemData
    // console.log(result)
    return result
}
// <---- Interfaces with other modules
export {registerNewUser, verify, getProfile, updateProfile, updateHistory, getAllUserHistory}
