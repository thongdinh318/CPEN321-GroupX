import * as server from "../server.js"
// const client = server.client

//const https = require("https");

// const url = "http://" + host + ":" + forumDB_port  + "/forums";
//ChatGPT usage: No
var forum_id = 1;
function dateAdded(){
	const date = new Date();
	let time = date.toLocaleDateString() + " " + date.toLocaleTimeString();	
	return time;
}

export default class ForumModule{
    //ChatGPT usage: No
    construtor(){
        this.dateCreated = new Date();
    }

    // DATABASE COMMUNICATION INTERFACES
    //ChatGPT usage: No
    createForum = async function(forumName){
        let forum = {
            id: forum_id,
            name : forumName,
        };
        forum.dateCreated = dateAdded();
        forum.comments = [];
        console.log(forum)
        await server.client.db('ForumDB').collection('forums').insertOne(forum);
        forum_id++;
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

    addCommentToForum = async function(forumId, commentData, username){
        var datePosted = dateAdded();
        let comment = {
            username,
            content : commentData,
            datePosted
        }
        const response = await server.client.db('ForumDB').collection('forums')
                        .updateOne({ id : forumId}, { $push:{ comments : comment }});
        console.log(response)
        return (response["modifiedCount"] !== 0);
    }

    //ChatGPT usage: No
    // addCommentToForum = async function(forumId, commentData, username, parent_id){
    //     var datePosted = dateAdded();
    //     let commentLevel;
    //     let comment_id = forumId + "_" + (Date.now());
    //     console.log(parent_id)
    //     if(parent_id == "none"){
    //         commentLevel = 0;
    //     }
    //     else{
    //         // Find the parent post and get its level
    //         let parentForum = await server.client.db("ForumDB").collection('forums').find({comments : {$elemMatch : {comment_id : parent_id}}}).toArray();
    //         let parentComment = parentForum[0].comments.find(o => o.comment_id === parent_id);

    //         let index = parentForum[0].comments.indexOf(parentComment);
    //         parentForum[0].comments[index].childArray.push(comment_id);
            
    //         const parentUpdate = await server.client.db('ForumDB').collection('forums').updateOne({ id : forumId}, {$set : {comments : parentForum[0].comments}});

    //         // If you try to reply to a comment at level 3 then it is proccessed as a sibling comment and not a child
    //         if(parentComment.commentLevel == 3){
    //             commentLevel = 3;
    //             parent_id = parentComment.parent_id;

    //         }else{
    //             commentLevel = parentComment.commentLevel + 1;
    //         }
    //     }

    //     let comment = {
    //         commentLevel: commentLevel, // max 3
    //         parent_id: parent_id, //Null if this is the first comment of the thread
    //         comment_id : comment_id,
    //         childArray : [],
    //         username,
    //         content : commentData,
    //         datePosted
    //     }
    //     console.log(comment)
    //     try{
    //         const response = await server.client.db('ForumDB').collection('forums')
    //                         .updateOne({ id : forumId}, { $push:{ comments : comment }});
            
    //         console.log(response)

    //         if(response["modifiedCount"] !== 0)
    //             return (comment_id);
    //         else
    //             return "err";
            
    //     }catch(err){

    //         return "err";
    //     }
    // }
}