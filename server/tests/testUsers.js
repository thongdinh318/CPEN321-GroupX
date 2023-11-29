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

const testUser4 = { 
	"userId": '4',
    "username": "test4",
	"dob": "11/19/2024",
	"email":"test4@gmail.com",
	"subscriptionList":["yahoo"],
	"history":[
		{
            "articleId": 1,
			"views": 100
		},
        {
            "articleId": 2,
			"views": 100
		},
        {
            "articleId": 3,
			"views": 100
		}
	]
}

export {testUser1, testUser2, testUser3, testUser4}