const express = require('express');
const bodyParser = require('body-parser');
var jsonParser = bodyParser.json();
let app = express();
const axios = require('axios');



//const https = require("https");
const https = require("http");
const host = '127.0.0.1';
const this_port = 8090;
const forumDB_port = 8080;
const other_module_port = 8070;

const url = "http://" + host + ":" + forumDB_port  + "/forums";


class ForumModule{
    construtor(){
        this.dateCreated = new Date();
    }

    // DATABASE COMMUNICATION INTERFACES
    createForum = async function(forumId, forumName){
        try{
            let data = {
                id: forumId,
                name : forumName,
            };
            const res = await axios.post(url, data);
            return res.data;
        }catch(err){
            console.log(err);
        }
    }



    getForum = async function (forumId){
        const response = await axios.get(url  + "/" + forumId);
        return response.data;
        
    }

    deleteForum = async function(forumId){
        const response = await axios.delete(url + "/" +forumId);
        return response.data;
        
    }

    deleteForums = async function(){
        const response = await axios.delete(url);
        return response.data;
        
    }

    addCommentToForum = async function(forumId, commentData, user){
        try{
            let data = {
                //username : user.username,
                username : user,
                content : commentData
            }
            const response = await axios.post("http://" + host + ":" +forumDB_port + "/addComment/" +  forumId, data);
            return true;
        }catch(err){
            console.log(err);
            return false;
        }

    }
};


// Testing

app.listen(this_port, async () => {
	console.log(`${new Date()}  Forum Services Started. Listening on ${host}:${this_port}`);
    console.log(url);
    fm = new ForumModule();
    //console.log(await fm.deleteForums());
    console.log(await fm.createForum("123", "economics"));
    console.log(await fm.createForum("456", "sports"));
    console.log(await fm.createForum("789", "economics"));

});




module.exports = ForumModule;