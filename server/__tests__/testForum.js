// const forum1 = 
// {
//   "name": "economics",
//   "comments": [
//     {
//       "username": "testuser1",
//       "content": "sample text",
//       "datePosted": "2023-10-31 6:57:53 p.m."
//     },
//     {
//       "username": "testuser2",
//       "content": "hello world",
//       "datePosted": "2023-10-31 6:59:13 p.m."
//     }
//   ],
//   "id": "123"
// };



// const forum2 = 
// {
//   "name": "politics",
//   "comments": [],
//   "id": "456"
// };

// const forum2_after = 
// {
//   "name": "politics",
//   "comments": [
//     {
//         "username" : "test1",
//         "content" : "test comment for POST /addComment"
//     }
//   ],
//   "id": "456"
// };



// const forum3 = 
// {
//   "name": "sports",
//   "comments": [
//     {
//       "username": "testuser1",
//       "content": "sample text",
//       "datePosted": "2023-10-31 6:57:53 p.m."
//     },
//     {
//       "username": "testuser2",
//       "content": "hello world",
//       "datePosted": "2023-10-31 6:59:13 p.m."
//     }
//   ],
//   "id": "789"
// };


// export {forum1, forum2, forum2_after, forum3}



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
      "commentLevel": 0,
      "parent_id": null,
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


export {forum1, forum2, forum3, forum1_after}