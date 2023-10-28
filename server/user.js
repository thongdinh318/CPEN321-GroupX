//Default user setting
const defUser = { 
	"userId": 0,
    "username": null,
	"dob": null,
	"email":null,
	"subscriptionList":[],
	"history":[
/*		{
            "articleId":null,
			"rating":null
		}*/
	]
	
}
// Helper Functions --->
async function checkAvailable(client, query, db, collection){
    try {
        var result = await client.db(db).collection(collection).find(query)
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

function createNewUser(userId){
    var newUser = JSON.parse(JSON.stringify(defUser))
    newUser.userId = userId
    newUser.username = "user"+ String(userId)
    newUser.email = newUser.username + "@gmail.com"
    return newUser
}

// <-- Helper Functions
//testing purpose -->
async function initDb(client, initNum){
    try {
        for (var id = 1; id < initNum; id++){
            var newUser = createNewUser(id)
            await client.db("userdb").collection("profile").insertOne(newUser)
        }
        
        return("success\n")
    } catch (error) {
        return (error)
    }
}
// <--- testing purpose

//Interfaces with frontend -->
// get profile
async function getProfile(client, userId){
    try {
        var user = await checkAvailable(client,{"userId":userId},"userdb","profile")
        if (user === null){
            console.log("New User registers\n")
            var newUser = createNewUser(userId)
            await client.db("userdb").collection("profile").insertOne(newUser);
            return (newUser)
        }
        else{
            delete user._id;
            return user
        }
    } catch (error) {
        console.log(error)
        return error
    }
}
//get Subscription list
async function getSubList(client, userId){
    try {
        var user = await checkAvailable(client, {"userId":userId},"userdb","profile")
        if (user == null){
            console.log("Not exist\n")
            return ("User " + String(userId) +  " doesn't exist")
        }
        else{
            return user.subscriptionList
        }
    } catch (error) {
        console.error(error);
        return error
    }
}
//Update profile info
async function updateProfile(client, userId, newProfile){
    try {
        var user = await checkAvailable(client, {"userId": userId},"userdb","profile")
        if (user == null){
            return ("User doesn't exist")
        }
        else{
            //TODO: sanitize inputs before update
            await client.db("userdb").collection("profile").updateOne({"userId":userId}, {$set: newProfile})
            return ("Update complete")
        }
    } catch (error) {
        console.error(error)
        return (error)
    }
}
//Update reading history
async function updateHistory(client, userId, data){
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
async function getAllUserHistory(client){
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

export {initDb, getProfile, getSubList, updateProfile, updateHistory, getAllUserHistory}
