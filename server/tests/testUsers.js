const testUser1 = { 
	"userId": '1',
    "username": "test1",
	"dob": "11/19/2023",
	"email":"test1@gmail.com",
	"subscriptionList":["cnn", "cbc"],
	"history":[
        {
            "articleId": 1,
			"views": 1
		},
        {
            "articleId": 2,
			"views": 1
		},
        {
            "articleId": 3,
			"views": 1
		},
        {
            "articleId": 4,
			"views": 1
		}

    ]
}
const testUser2 = { 
	"userId": '2',
    "username": "test2",
	"dob": "12/19/2023",
	"email":"test2@gmail.com",
	"subscriptionList":[],
	"history":[
        {
            "articleId": 1,
			"views": 1
		},
        {
            "articleId": 2,
			"views": 1
		}

    ]
}
const testUser3 = { 
	"userId": '3',
    "username": "test3",
	"dob": "11/19/2024",
	"email":"test3@gmail.com",
	"subscriptionList":["cbc"],
	"history":[]
}

export {testUser1, testUser2, testUser3}