/**
 * This server communicates with the forum database and the main part of the forums module
 */


const express = require('express');
const { MongoClient, Double } = require('mongodb');	
const bodyParser = require('body-parser');
var jsonParser = bodyParser.json();

const host = '127.0.0.1';
const port = 8080;

let app = express();
app.use(jsonParser);
const uri = 'mongodb://127.0.0.1:27017';
const client = new MongoClient(uri);





//--------------------------------------------------------------------------------------------------
// Get all forums
app.get("/forums", async (req, res) =>{
	try{
		const result = await client.db("ForumDB").collection("forums").find().sort({ 'rating' : -1 }).toArray();

		res.status(200).send(result);
	} catch (err){
		console.log(err);
		res.status(400).send("No Forums")
	}
}); 


// GET one specific forum, queried with forum id
app.get("/forums/:forum_id", async (req, res) =>{
	try{

		const result = await client.db("ForumDB").collection("forums").find({id : req.params.forum_id}).toArray();
		res.status(200).send(result);

	} catch (err){

		console.log(err);
		res.status(400).send("Forum was not found");

	}
});  

//--------------------------------------------------------------------------------------------------




// Add a new forum
app.post("/forums",async (req, res)=>{
	try{
		//console.log(req.body);
		let forum = req.body;
		forum.dateCreated = dateAdded();
		forum.comments = [];
		let result = await client.db('ForumDB').collection('forums').insertOne(forum);
		res.status(200).send(forum);
	}catch (err){
		console.log(err);
		res.status(400).send("Could not add forum");
	}
} );


//--------------------------------------------------------------------------------------------------






// Post a comment to a forum
app.post("/addComment/:forum_id",async (req, res)=>{
	try{
		let comment = req.body;
		comment.datePosted = dateAdded();
		await client.db('ForumDB').collection('forums').updateOne({ id : req.params.forum_id}, { $push:{ comments : comment }});

		res.status(200).send("Comment Posted!");

	}catch (err){
		console.log(err);
		res.status(400).send("Could not post comment");
	}
} );


//--------------------------------------------------------------------------------------------------




// Deletes all forums 
app.delete("/forums" , async (req, res)=>{
	try{
		await client.db('ForumDB').collection('forums').drop({});
		res.status(200).send("All forums were deleted");

	}catch(err){
		console.log(err);
		res.status(400).send("Could not delete forums.");
	}

});

// Delete one specific forum
app.delete("/forums/:forum_id", async(req,res) =>{
	try{
		await client.db("ForumDB").collection("forums").deleteOne({id : req.params.forum_id});
		res.status(200).send("Forum " +  req.params.forum_id + " was deleted");
	}catch (err){
		res.status(400).send("Could not delete specified forumm.");
	}
})



// Clears all forums 
app.delete("/forumComments" , async (req, res)=>{
	try{
		let forums = await client.db("ForumDB").collection("forums").find().sort({ 'rating' : -1 }).toArray();
		for(let i = 0; i < forums.length; i++){
			await client.db('ForumDB').collection('forums').updateOne({ id : forums[i]["id"]}, { $set:{ comments : [] }});
		}
		res.status(200).send("All forums were cleared.");

	}catch(err){
		console.log(err);
		res.status(400).send("Could not delete forums.");
	}

});


//--------------------------------------------------------------------------------------------------




app.listen(port, () => {
	console.log(`${new Date()}  ForumDB communcation. Listening on ${host}:${port}`);

});



function dateAdded(){
	const date = new Date();
	let time = date.toLocaleDateString() + " " + date.toLocaleTimeString();	
	return time;

}




