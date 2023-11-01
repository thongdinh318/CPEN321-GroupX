import { MongoClient } from "mongodb";
//Default user setting
const defUser = { 
	"userId": 0,
    "username": "root",
	"dob": null,
	"email":null,
	"subscriptionList":[],
	"history":[
/*		{
            "articleId":null,
			"views":null
		}*/
	]
	
}
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

// Helper Functions --->
// checkAvaulable is buggy 
// we don't need it since we are using try-catch 
// we can have a special response in case of errors
async function checkAvailable(query, db, collection){
    try {
        
        var result = await client.db(db).collection(collection).find(query).toArray()
        console.log(result)
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

function createNewUser(userId){

    var newUser = JSON.parse(JSON.stringify(defUser))
    newUser.userId = userId
    newUser.username = "user"+ String(userId)
    newUser.email = newUser.username + "@gmail.com"
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




// get profile
// async function getProfile(userId){
//     try {
//         var user = await checkAvailable(client,{"userId":userId},"userdb","profile")
//         if (user === null){
//             console.log("New User registers\n")
//             var newUser = createNewUser(userId)
//             await client.db("userdb").collection("profile").insertOne(newUser);
//             return (newUser)
//         }
//         else{
//             delete user._id;
//             return user
//         }
//     } catch (error) {
//         console.log(error)
//         return error
//     }
// }



async function getProfile(userId){
    try{
        const result = await client.db("userdb").collection("profile").find({"userId" : userId}).toArray();
        return result[0];
    } catch(err){
        return err;
    }
    
}

async function addUser(userId, profile){
    try{
        var newUser = {
            "userId" : userId,
            "username" : profile.username,
            "dob" : profile.dob,
            "email" : profile.email, 
            "subscriptionList" : [],
            "history" : []
        }
        const result = await client.db("userdb").collection("profile").insertOne(newUser);
        return result;
    } catch(err){
        return err;
    }
}



//get Subscription list
async function getSubList(userId){
    try {
        // var user = await checkAvailable(client, {"userId":userId},"userdb","profile")
        // if (user == null){
        //     console.log("Not exist\n")
        //     return ("User " + String(userId) +  " doesn't exist")
        // }
        // else{
        //     return user.subscriptionList
        // }

        const user = await client.db("userdb").collection("profile").find({"userId" : userId}).toArray();
        return user[0].subscriptionList;
    } catch (error) {
        console.error(error);
        return error
    }
}



//Update profile info
async function updateProfile(userId, newProfile){
    try {
        // var user = await checkAvailable(client, {"userId": userId},"userdb","profile")
        // if (user == null){
        //     return ("User doesn't exist")
        // }
        // else{
        //     //TODO: sanitize inputs before update
        //     await client.db("userdb").collection("profile").updateOne({"userId":userId}, {$set: newProfile})
        //     return ("Update complete")
        // }
        await client.db("userdb").collection("profile").updateOne({"userId":userId}, {$set: {
            "userId" : newProfile.userId,
            "username" : newProfile.username,
            "dob" : newProfile.dob,
            "email" : newProfile.email, 
            "subscriptionList" : newProfile.subscriptionList,
            "history" : newProfile.history
        }});
        return ("Update complete");

    } catch (error) {
        console.error(error)
        return (error)
    }
}




//Update reading history
async function updateHistory(userId, data){
    try {
        var user = await checkAvailable(client, {"userId": userId}, "userdb", "profile")
        if (user == null){
            res.status(200).send("User doesn't exist")
        }
        else{
            // const newHistory = req.body
            const newHistory = data
            var oldHistory = user.history
            var updateHistory = new Set()
            oldHistory.forEach(element => {
                updateHistory.add(element)
            });
            updateHistory = newHistory.filter((update)=>{
                var dup = updateHistory.has(update.articleId)
                updateHistory.add(dup)
                return !dup
            })
            await client.db("userdb").collection("profile").updateOne({"userId":userId}, 
            {$set: {"history":updateHistory}})
            return ("Finish")
        }
    } catch (error) {
        return (error)
    }
}

// <--Interfaces with frontend 

// Interfaces with other modules -->
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

export {initUDb, getProfile, getSubList, updateProfile, updateHistory, getAllUserHistory, addUser}
