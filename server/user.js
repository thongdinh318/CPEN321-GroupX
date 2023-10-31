import { MongoClient } from "mongodb";
import {OAuth2Client} from 'google-auth-library'

const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

const ggClient = new OAuth2Client();
const CLIENT_ID = 1 //TODO: replace witht the real client id
//Default user setting
const defUser = { 
	"userId": 0,
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
async function checkAvailable(userId){
    try {
        var result = await client.db("userdb").collection("profiles").find({"userId":userId})
        var arr = await result.toArray()
        if (arr == undefined || arr.length == 0){
            return null
        }
        else{
            return arr[0]
        }
    } catch (error) {
        throw(error)
    }    

}

function createNewUser(userId, userName, userEmail){
    var newUser = JSON.parse(JSON.stringify(defUser))
    newUser.userId = userId
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
async function verify(token){
    try {
        const ticket =  await ggClient.verifyIdToken({
            idToken: token,
            audience: CLIENT_ID
        });
    
        const payload =ticket.getPayLoad();
        const userId = payload['sub']

        const userProfile = await checkAvailable(userId)
    
        if (userProfile != null){
            return userProfile
        }
        else{
            console.log("New User registers\n")
            const userEmail = payload['email']
            const userName = payload['name']
            var newUser = createNewUser(userId, userName,userEmail)
            await client.db("userdb").collection("profile").insertOne(newUser);
            return (newUser)
        }
    } catch (error) {
        return(error)
    }
}
// get profile
async function getProfile(userId){
    try {
        var user = await checkAvailable({"userId":userId})
        return user
    } catch (error) {
        return error
    }
}
//Update profile info
async function updateProfile(userId, newProfile){
    try {
        var user = await checkAvailable(userId)
        if (user == null){
            return null
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
async function updateHistory(userId, newViewed){
    try {
        var user = await checkAvailable(userId)
        if (user == null){
            return null
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
        return (error)
    }
}

// <--Interfaces with frontend 

// Interfaces with other modules -->
//Get the reading history of all users
//Used by Recommendation module
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

export {initUDb, verify, getProfile, updateProfile, updateHistory, getAllUserHistory}
