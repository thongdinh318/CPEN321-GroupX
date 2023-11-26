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
        this.dateCreated = new Date();
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
    // deleteForum = async function(forumId){
    //     // const response = await axios.delete(url + "/" +forumId);
    //     // return response.data;
    //     // console.log("Delete forum " + forumId)
    //     // try{
    //         const result = await server.client.db("ForumDB").collection("forums").deleteOne({id : forumId});
    //         return result.acknowledged
    
    //     // }catch(err){
    //     //     return(err);
    //     // }
        
    // }

    //ChatGPT usage: No
    // deleteForums = async function(){
    //     // const response = await axios.delete(url);
    //     // return response.data;
    //     // console.log("Delete all forums")
    //     // try{
    //         const result = await server.client.db('ForumDB').collection('forums').drop({});
            
    //         return result
    
    //     // }catch(err){
    //     //     return (err)
    //     // }
        
    // }

    //ChatGPT usage: No
    addCommentToForum = async function(forumId, commentData, username, parent_id){
        var datePosted = dateAdded();
        console.log("parent_id = " + parent_id )

        let commentLevel;
        if(parent_id == null)
            commentLevel = 0;

        else{
            // Find the parent post and get its level
            // this does not work right now
            const parentForum = await server.client.db("ForumDB").collection('forums').find({comments : {$elemMatch : {comment_id : parent_id}}}).toArray();
            console.log(parentForum[0].comments.find(o => o.comment_id === parent_id));
            let parentComment = parentForum[0].comments.find(o => o.comment_id === parent_id);
            console.log("parentComment.commentLevel = " + parentComment.commentLevel);
            
            if(parentComment.commentLevel == 3){
                commentLevel = 3;
                parent_id = parentComment.parent_id;

            }else{
                commentLevel = parentComment.commentLevel + 1;
            }
            
        }
        

        let comment = {
            
            commentLevel: commentLevel, // max 3
            parent_id: parent_id, //Null if this is the first comment of the thread
            comment_id : forumId + "_" + (Date.now()),

            username,
            content : commentData,
            datePosted
        }
        
        try{
            const response = await server.client.db('ForumDB').collection('forums')
                            .updateOne({ id : forumId}, { $push:{ comments : comment }});

            return (response["modifiedCount"] !== 0);
        }catch(err){
            // console.log(err);
            return false;
        }
    }
}
