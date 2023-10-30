/**
 * This server communicates with the forum database and the main part of the forums module
 */


const express = require('express');
	
const bodyParser = require('body-parser');
var jsonParser = bodyParser.json();
import ForumModule from './forum_interface.js';
import { getProfile } from '../user.js';

const forum = new ForumModule()
// const host = '127.0.0.1';
// const port = 8080;

let app = express();
app.use(jsonParser);


function isErr(error){
    //https://stackoverflow.com/questions/30469261/checking-for-typeof-error-in-js
    return error, error.e, error.stack
}
//--------------------------------------------------------------------------------------------------
// Get all forums
app.get("/forums", async (req, res) =>{
	try{
		const result = await forum.getAllForums();
		if (isErr(result)){
			res.status(400).send("Cannot get forum list")
		}
		else{
			res.status(200).send(result);
		}
	} catch (err){
		console.log(err);
		res.status(400).send("No Forums")
	}
}); 

// GET one specific forum, queried with forum id
app.get("/forums/:forum_id", async (req, res) =>{
	try{
		
		const result =await forum.getForum(req.params.forum_id)
		// const result = await client.db("ForumDB").collection("forums").find({id : req.params.forum_id}).toArray();
		if (isErr(result)){
			res.status(400).send(result)
		}
		else{
			res.status(200).send(result);
		}

	} catch (err){

		console.log(err);
		res.status(400).send("Forum was not found");

	}
});  

//--------------------------------------------------------------------------------------------------
// Post a comment to a forum
app.post("/addComment/:forum_id",async (req, res)=>{
	try{
		let commentData = req.body.commentData;
		let userId = req.body.userId
		const user = await getProfile(userId)
		
		const result = await forum.addCommentToForum(req.params.forum_id, commentData, user.username)
		// await client.db('ForumDB').collection('forums').updateOne({ id : req.params.forum_id}, { $push:{ comments : comment }});

		if (isErr(result)){
			res.status(400).send("Could not post comment")
		}
		else{
			if (result){
				res.status(200).send("Comment Posted!");
			}
			else{
				res.status(200).send("Failed Posting Comment! Please Try Again")
			}
		}

	}catch (err){
		res.status(400).send(err);
	}
} );


//--------------------------------------------------------------------------------------------------

// These are server functions for now, might be implemented as an endpoint for use after MVP
// Add a new forum
// app.post("/forums",async (req, res)=>{
// 	try{
// 		//console.log(req.body);
// 		let forumId = req.body.id;
// 		let forumName = req.body.name
// 		let addedForum = await forum.createForum(forumId, forumName);
// 		res.status(200).send(addedForum);
// 	}catch (err){
// 		console.log(err);
// 		res.status(400).send("Could not add forum");
// 	}
// } );
// Deletes all forums 
// app.delete("/forums" , async (req, res)=>{
// 	try{
// 		const succeed = await forum.deleteForums();
// 		if (isErr(succeed)){
// 			res.status(400).send("Error when deleting")
// 		}

// 		if (succeed){
// 			res.status(200).send("All forums were deleted");
// 		}
// 		else {
// 			res.status(200).send("All Forum Removal Failed! Please try again")
// 		}

// 	}catch(err){
// 		console.log(err);
// 		res.status(400).send("Could not delete forums.");
// 	}

// });

// Delete one specific forum
// app.delete("/forums/:forum_id", async(req,res) =>{
// 	try{
// 		const succeed = await forum.deleteForum(req.params.forum_id);
// 		if (isErr(succeed)){
// 			res.status(400).send(succeed)
// 		}
// 		else{
// 			if (succeed){
// 				res.status(200).send("Remove succeed")
// 			}
// 			else{
// 				res.status(200).send("Removal Failed! Please try again")
// 			}
// 		}

// 	}catch (err){
		
// 	}
// })

// Clears all forums
// app.delete("/forumComments" , async (req, res)=>{
// 	try{
// 		let forums = await client.db("ForumDB").collection("forums").find().sort({ 'rating' : -1 }).toArray();
// 		for(let i = 0; i < forums.length; i++){
// 			await client.db('ForumDB').collection('forums').updateOne({ id : forums[i]["id"]}, { $set:{ comments : [] }});
// 		}
// 		res.status(200).send("All forums were cleared.");

// 	}catch(err){
// 		console.log(err);
// 		res.status(400).send("Could not delete forums.");
// 	}

// });


//--------------------------------------------------------------------------------------------------

// app.listen(port, () => {
// 	console.log(`${new Date()}  ForumDB communcation. Listening on ${host}:${port}`);

// });




