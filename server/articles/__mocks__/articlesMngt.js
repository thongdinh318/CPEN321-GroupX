
export default function searchById(articleId){
    if(articleId == "1") {
        return {
            "articleId": 1,
            "views": 0,
            "content": "test article",
            "categories":[],
            "publisher":"random pub",
            "publishedDate": null,
        }
    } else if(articleId == "999") {
        return {}
    }
}

export default function searchByFilter(query){
    
}