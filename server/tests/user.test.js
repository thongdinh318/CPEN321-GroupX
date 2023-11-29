import {expect, test, jest} from "@jest/globals" 
import { app, server } from "../server.js";
import supertest from "supertest";
import { MongoClient } from "mongodb";
import { testUser1, testUser2, testUser3 } from "./testUsers.js";

//Mock userdb
let connection
let db
// let fdb;
beforeAll(async()=>{
    const uri = "mongodb://127.0.0.1:27017";
    connection = await MongoClient.connect(uri);
    // fdb = connection.db("ForumDB");
    db = connection.db("userdb");
    await db.collection("profile").insertMany([testUser1, testUser2, testUser3]);
    
});

afterAll(async ()=>{
    await db.collection('profile').deleteMany({});
    await connection.close()
    server.close()
});

// Interface GET /profile/:userId 
describe("GET /profile/:userId", ()=>{
    //Chat GPT Usage: No
    test('valid user id', async ()=>{
        // Input: userId that is contained in database
        // Expected status code: 200
        // Expected behavior: user profile is retrieved
        // Expected output: object with user details

        const res = await supertest(app).get("/profile/1");
        delete testUser1._id
        expect(res.status).toBe(200);
        expect(res.body).toStrictEqual(testUser1);
    });
    //Chat GPT Usage: No
    test('invalid user id', async ()=>{
        // Input: userId that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing is retrieved
        // Expected output: error message: “User Profile not Found”

        const res = await supertest(app).get("/profile/4");
        expect(res.status).toBe(400);
        expect(res.text).toBe("User Profile not Found")
    });
});

//Interface GET /profile/:userId/subscriptions
describe("GET /profile/:userId/subscriptions", ()=>{
    //Chat GPT Usage: No
    test('valid user id', async ()=>{
        // Input: userId that is contained in database
        // Expected status code: 200
        // Expected behavior: user’s subscription list is retrieved
        // Expected output: user’s subscription list

        const res = await supertest(app).get("/profile/1/subscriptions");
        expect(res.status).toBe(200);
        expect(res.body).toStrictEqual(testUser1.subscriptionList);
    });
    //Chat GPT Usage: No
    test('invalid user id', async ()=>{
        // Input: userId that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing is retrieved from the database
        // Expected output: an empty array

        const res = await supertest(app).get("/profile/4/subscriptions");
        expect(res.status).toBe(400);
        expect(res.body).toStrictEqual([])
    });
});

// Interface GET /profile/:userId/history 
describe("GET /profile/:userId/history", ()=>{
    //Chat GPT Usage: No
    test('valid user id', async ()=>{
        // Input: userId that is contained in database
        // Expected status code: 200
        // Expected behavior: user’s history is retrieved
        // Expected output: articles in user’s history

        const res = await supertest(app).get("/profile/1/history");
        // console.log(res.body)
        expect(res.status).toBe(200);
        // expect(res.body).toStrictEqual(testUser1.history);
    });
    //Chat GPT Usage: No
    test('invalid user id', async ()=>{
        // Input: userId that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing is retrieved from the database
        // Expected output: an empty array

        const res = await supertest(app).get("/profile/4/history");
        expect(res.status).toBe(400);
        expect(res.body).toStrictEqual([])
    });
});

// Interface PUT /profile/:userId
describe("PUT /profile/:userId", ()=>{
    let newSubList = ["yahoo", "cnn"] 
    let payload = {"subscriptionList": newSubList}
    //Chat GPT Usage: No
    test('valid user id', async ()=>{
        // Input:  a valid userId
        // Expected status code: 200 
        // Expected behavior: User information is updated accordingly in the database
        // Expected output: success message: “Profile was updated”

        const res = await supertest(app).put("/profile/1").send(payload);
        const testUser = await db.collection("profile").findOne({"userId":"1"});
        expect(res.status).toBe(200);
        expect(res.text).toBe("Profile was updated");
        expect(testUser.subscriptionList).toStrictEqual(newSubList)

    });
    //Chat GPT Usage: No
    test('invalid user id', async ()=>{
        // Input:  userId that is not contained in database
        // Expected status code: 400 
        // Expected behavior: Database is not updated
        // Expected output: error message: "Cannot Update Profile/User not found"

        const res = await supertest(app).put("/profile/4").send(payload);
        expect(res.status).toBe(400);
        expect(res.text).toBe("Cannot Update Profile/User not found")
    });
});

// Interface PUT /profile/:userId/history
describe("PUT /profile/:userId/history", ()=>{
    //Chat GPT Usage: No
    test('valid user id and new article read', async ()=>{
        // Input: userId that is contained in database, and the articleId does not exist in the user’s history
        // Expected status code: 200
        // Expected behavior: new article with views = 1 is added to the user’s history
        // Expected output: success message: “Article added to history”

        let newHistory = {"articleId":5}
        const res = await supertest(app).put("/profile/1/history").send(newHistory);
        // console.log(res.body)
        expect(res.status).toBe(200);
        expect(res.text).toBe("Article added to history");
        const testUser = await db.collection("profile").findOne({"userId":"1"});
        let historyElement = testUser.history.find((article) => article.articleId == 5)
        expect(historyElement.views).toBe(1)

    });
    //Chat GPT Usage: No
    test('valid user id and old article read', async ()=>{
        // Input: userId that is contained in database, and the articleId exists in the user’s history
        // Expected status code: 200
        // Expected behavior: the views of the article increase by 1 in the user’s history
        // Expected output: success message: “Article added to history”

        let newHistory = {"articleId":1}
        const res = await supertest(app).put("/profile/1/history").send(newHistory);
        // console.log(res.body)
        expect(res.status).toBe(200);
        expect(res.text).toBe("Article added to history");
        const testUser = await db.collection("profile").findOne({"userId":"1"});
        let historyElement = testUser.history.find((article) => article.articleId == 1)
        expect(historyElement.views).toBe(2)

    });
    //Chat GPT Usage: No
    test('invalid user id', async ()=>{
        // Input: userId that is not contained in database
        // Expected status code: 400
        // Expected behavior: nothing changed in the database
        // Expected output: error message: “Cannot Update History/User not found”

        const res = await supertest(app).put("/profile/4/history").send({"articleId":5});
        expect(res.status).toBe(400);
        expect(res.text).toBe("Cannot Update History/User not found")
    });
});
// Interface POST /signin
describe('POST /signin', ()=>{
    const newUser = { 
        "userId": '99',
        "username": "user99",
        "dob": null,
        "email":"user99@gmail.com",
        "subscriptionList":[],
        "history":[]
    }
    //Chat GPT Usage: Partial
    test('new user login', async()=>{
        // Input:  a valid idToken
        // Expected status code: 200 
        // Expected behavior: create a new User and add the new user to db
        // Expected output: return the new User just created
        const res = await supertest(app).post("/signin").send({idToken:"valid_token"});
        // console.log(res.body)
        expect(res.status).toBe(200)
        expect(res.body).toStrictEqual(newUser)
    })
    //Chat GPT Usage: Partial
    test('old user login', async()=>{
        // Input:  a valid idToken of user that has registered before
        // Expected status code: 200 
        // Expected behavior: do nothing
        // Expected output: return the old user from the db
        const res = await supertest(app).post("/signin").send({idToken:"valid_token"});
        // console.log(res)
        expect(res.status).toBe(200)
        expect(res.body).toStrictEqual(newUser)
    })
    //Chat GPT Usage: Partial
    test('invalid token', async()=>{
        // Input:  an invalid idToken
        // Expected status code: 400
        // Expected output: return the message saying the "invalid token"
        const res = await supertest(app).post("/signin").send({idToken:"invalid_token"});
        // console.log(res)
        expect(res.status).toBe(400)
        expect(res.text).toBe("invalid token")
    })
    //Chat GPT Usage: Partial
    test('error token', async()=>{
        // Input: an idToken that gives error when verifying
        // Expected status code: 400
        // Expected output: return the message saying the "error token"
        const res = await supertest(app).post("/signin").send({idToken:"error_token"});
        // console.log(res)
        expect(res.status).toBe(400)
        expect(res.text).toBe("error token")
    })

});

