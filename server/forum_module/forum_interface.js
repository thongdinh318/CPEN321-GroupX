import * as server from "../server.js"
// const client = server.client

//const https = require("https");

// const url = "http://" + host + ":" + forumDB_port  + "/forums";
//ChatGPT usage: No
function dateAdded(){
	const date = new Date();
	let time = date.toLocaleDateString() + " " + date.toLocaleTimeString();	
	return time;
}

export default class ForumModule{
    //ChatGPT usage: No
    construtor(){
        // this.dateCreated = new Date();
    }

    // DATABASE COMMUNICATION INTERFACES
    //ChatGPT usage: No
    createForum = async function(forumId, forumName){
        let forum = {
            id: forumId,
            name : forumName,
        };
        forum.dateCreated = dateAdded();
        forum.comments = [];
        await server.client.db('ForumDB').collection('forums').insertOne(forum);
        return forum;
    }

    //ChatGPT usage: No
    getAllForums = async function(){
        // console.log("Get all forums")
        // try{
            const result = await server.client.db("ForumDB").collection("forums").find().sort({ 'rating' : -1 }).toArray();
            
            return (result);
        // } catch (err){
        //     return(err)
        // }
    }
    //ChatGPT usage: No
    getForum = async function (forumId){
        // const response = await axios.get(url  + "/" + forumId);
        // console.log("Get forum" + forumId)
        // try{
            const result = await server.client.db("ForumDB").collection("forums").find({id : forumId}).toArray();
    
            return (result);
        // } catch (err){
        //     return(err)
        // }
        // return response.data;
        
    }

    
    //ChatGPT usage: No
    addCommentToForum = async function(forumId, commentData, username){
        var datePosted = dateAdded();
        

        let comment = {
            username,
            content : commentData,
            datePosted
        }
        if(comment.username == undefined){
            return false;
        }
            
        
        const response = await server.client.db('ForumDB').collection('forums')
                        .updateOne({ id : forumId}, { $push:{ comments : comment }});

        return (response["modifiedCount"] !== 0);

    }
}