import { MongoClient } from "mongodb";
import {OAuth2Client} from 'google-auth-library'

const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

const CLIENT_ID = "474807609573-3rub2rf78k2tirh75j9ivh9u16b7uor7.apps.googleusercontent.com"//TODO: replace witht the real client id
const ggClient = new OAuth2Client(CLIENT_ID);
//Default user setting
const defUser = { 
	"userId": '0',
    "username": "root",
	"dob": null,
	"email":null,
	"subscriptionList":[],
	"history":[
        /*{
            "articleId":null,
            "title": null,
			"views":null
		}*/
	]
}

// Helper Functions --->
//ChatGPT usage: No
async function checkAvailable(userId){
    try {
        var result = client.db("userdb").collection("profile").find({"userId":userId})
        var arr = await result.toArray()
        if (arr == undefined || arr.length == 0){
            return new Object()
        }
        else{
            return arr[0]
        }
    } catch (error) {
        throw(error)
    }    

}
//ChatGPT usage: No
function createNewUser(userId, userName, userEmail){
    var newUser = JSON.parse(JSON.stringify(defUser))
    newUser.userId = userId
    newUser._id = userId
    if (userName == undefined){
        newUser.username = "default user name"
    }
    else{
        newUser.username = userName
    }
    newUser.email = userEmail
    return newUser
}
// <-- Helper Functions

//Init DB function --->
//ChatGPT usage: No
async function initUDb(){
    try {
        await client.db("userdb").collection("profile").insertOne(defUser)
        return("success\n")
    } catch (error) {
        return (error)
    }
}
//<--- Init DB function

//Interfaces with frontend -->
//ChatGPT usage: No
function verify(token){
    return new Promise((resolve, reject)=>{
        ggClient.verifyIdToken(
            {idToken: token, audience: CLIENT_ID},
            function(err, login){
                if (err){
                    throw err
                }
                if (login){
                    const payload = login.getPayload()
                    resolve(payload)
                }
                else{
                    reject("invalid token")
                }
            })
    })
}
//ChatGPT usage: No
async function registerNewUser(userId, username, userEmail){
    const userProfile = await checkAvailable(userId)

    if (userProfile.userId){
        console.log("Old User")
        return userProfile
    }
    else{
        console.log("New User")
        var newUser = createNewUser(userId, username,userEmail)
        await client.db("userdb").collection("profile").insertOne(newUser);
        return (newUser)
    }

}

// get profile
//ChatGPT usage: No
async function getProfile(userId){
    try {
        var user = await checkAvailable(userId)
        return user
    } catch (error) {
        return error
    }
}
//Update profile info
//ChatGPT usage: No
async function updateProfile(userId, newProfile){
    try {
        var user = await checkAvailable(userId)
        if (user.userId == undefined){
            return false
        }
        else{
            //TODO: sanitize inputs before update
            const result = await client.db("userdb").collection("profile").updateOne({"userId":userId}, {$set: newProfile})
            return (result.acknowledged)
        }
    } catch (error) {
        return (error)
    }
}


//Update reading history
// ChatGPT usage: No.
async function updateHistory(userId, newViewed){
    try {
        var user = await checkAvailable(userId)
	    console.log(user)
        if (user.userId == undefined){
            return false
        }
        else{
            var dup = false
            var updateHistory = user.history
            for (var pastViewed of updateHistory){
                if (pastViewed.articleId === newViewed.articleId){
                    pastViewed.views = pastViewed + 1
                    dup = true
                    break;
                }
            }

            if (!dup){
                newViewed.views = 1 
                updateHistory.push(newViewed)
            }

            const result = await client.db("userdb").collection("profile").updateOne({"userId":userId}, 
            {$set: {"history":updateHistory}})
            
            return (result.acknowledged)
        }
    } catch (error) {
	    console.log(error)
        return (error)
    }
}

// <--Interfaces with frontend 

// Interfaces with other modules -->


//Get the reading history of all users
//Used by Recommendation module
// ChatGPT usage: No.
async function getAllUserHistory(){
    var profileCollec = await client.db("userdb").collection("profile").find({}).toArray()
    
    var userItemData = [];
    var userList = [];

    profileCollec.forEach((user)=>{
        userList.push(user.userId);
        if (user.history.length > 0)
        {
            user.history.forEach((item)=>{
                var itemData = new Object()
                itemData.userId = user.userId;
                itemData.itemId = item.articleId;
                itemData.views = item.views;
                userItemData.push(itemData)
            })
        }
    })

    var result = new Object();
    result.users = userList;
    result.userItemData = userItemData
    console.log(result)
    return result
}
// <---- Interfaces with other modules

export {initUDb, registerNewUser, verify, getProfile, updateProfile, updateHistory, getAllUserHistory}
