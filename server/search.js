var express = require("express")
var app = express()

const {MongoClient} = require("mongodb")
const uri = "mongodb://127.0.0.1:27017"
const client = new MongoClient(uri)

app.use(express.json())

app.get("/",(req,res)=>{
    res.send("Hello World")
})

//Search by keyword or filters
app.get("/search", (req, res)=>{
    var filters = req.body
    // getArticleByCriteria(filters) - use the interface of arti managemnet here
})

//Search by id
app.get("/search/:articleId", (req, res)=>{
    var articleId = parseInt(req.originalUrl.substring(8), 10)
    // getArticleById(articleId)- use the interface of arti managemnet here
})

// Get trending
app.get("/search/trend", (req,res)=>{
    // TODO: implement here
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