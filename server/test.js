import express from "express";
import { MongoClient } from "mongodb";
import https from "https"
import http from "http"
import { ForumModule } from "./forum_module/forum_interface.js";
import WebSocket, {WebSocketServer} from "ws";
import * as userMod from "./user.js"

const forum = new ForumModule();

const uri = 'mongodb://127.0.0.1:27017';
const client = new MongoClient(uri);

// import client from "./server/mongoClient.js"

var app = express()
app.use(express.json());






// const server = http.createServer(app)
const server = http.createServer(app)
const wss = new WebSocketServer({ port: 9000 });




wss.on('connection', async (ws) => {
  console.log('A new client Connected!');

  ws.on('message', async (comment, isBinary) =>{

    comment = JSON.parse(comment);

    // console.log(comment);

    let commentData = comment.content;
	let userId = comment.userId
	const user = await userMod.getProfile(userId)
    let forum_id = comment.forum_id
    
    const res = await forum.addCommentToForum(forum_id, commentData, user.username).then()

    // if res is err
    if(!res) {

        ws.send("Could not post comment")
    }else{
        try{
            const newForum = await forum.getForum(forum_id);
            // console.log(newForum)
            // ws.send(newForum, {binary : isBinary});
            wss.clients.forEach(async (socketClient)=>{
                if (socketClient !== ws && socketClient.readyState === WebSocket.OPEN){
                    socketClient.send(newForum);
                }
            });
            
        }catch{
            ws.send("Could not post comment")
        }
    }
    
  });
});





async function run(){
    try {
        await client.connect()
        console.log("Successfully connect to db")
        server.listen(8081)


    } catch (error) {
        console.log(error)
        await client.close()
    }
}
run()




