var express = require("express")
var app = express()

const {MongoClient} = require("mongodb")
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

app.use(express.json())

app.get("/",(req,res)=>{
    res.send("Hello World")
})

//Default user setting
const defUser = { 
	"userId": 0,
    "username": null,
	"dob": null,
	"email":null,
	"subscriptionList":[],
	"interestProfile":[
		{
			"genre": null,
			"time" : null,
			"author": null,
			"topic": null,
			"keyWords": [],
			"views":null
		}
	]
	
}
// -------------------------------------------Helper Functions---------------------------------------------
async function checkAvailable(query, db, collection){
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

// -------------------------------------------Helper Functions END-----------------------------------------
//testing purpose -->
app.post("/initdb", async (req,res)=>{
    var initNum = req.body.initNum
    try {
        for (var id = 1; id < initNum; id++){
            var newUser = createNewUser(id)
            await client.db("userdb").collection("profile").insertOne(newUser)
        }
        
        res.status(200).send("success\n")
    } catch (error) {
        console.log(error)
        res.status(400).send(err)
    }
})
// <---

// get profile
app.get("/profile/:userId", async (req,res)=>{
    var userId = req.originalUrl.substring(9)
    userId = parseInt(userId, 10)
    try {
        var user = await checkAvailable({"userId":userId},"userdb","profile")
        if (user === null){
            console.log("New User registers\n")
            var newUser = createNewUser(userId)
            await client.db("userdb").collection("profile").insertOne(newUser);
            res.send(newUser)
        }
        else{
            delete user._id;
            res.status(200).send(user)
        }
    } catch (error) {
        console.log(error)
        res.status(400).send(error)
    }
})

//get Subscription list
app.get("/profile/:userId/subscriptions", async (req,res)=>{
    var userId = req.originalUrl.substring(9)
    userId = parseInt(userId, 10)
    try {
        console.log("Hi")
        var user = await checkAvailable({"userId":userId},"userdb","profile")
        console.log(user)
        if (user == null){
            console.log("Not exist\n")
            res.status(200).send("User " + String(userId) +  " doesn't exist")
        }
        else{
            res.status(200).send(user.subscriptionList)
        }
    } catch (error) {
        console.error(error);
        res.status(400).send(error)
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
