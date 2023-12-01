const forum1 = {
  "id": 1,
  "name": "General News",
  "comments": []
};


const forum1_after = {
  "id": 1,
  "name": "General News",
  "comments": [
    {
      "username" : "root",
      "content" : "test comment for POST /addComment"
    }
  ]
};


const forum2 = {
  "id": 2,
  "name": "Economics",
  "comments": []
};



const forum3 = {
  "id": 3,
  "name": "Education",
  "comments": []
};


const comment1 = {
        forum_id : 1,
        userId : "0",
        commentData : "test comment for sockets"
};

const comment2_bad_forumId = {
  forum_id : 99,
  userId : "0",
  commentData : "test comment for POST /addComment"
};

const comment3_bad_userId = {
  forum_id : 1,
  userId : "200",
  commentData : "test comment for POST /addComment"
};



export {forum1, forum2, forum3, forum1_after, comment1, comment2_bad_forumId ,comment3_bad_userId}