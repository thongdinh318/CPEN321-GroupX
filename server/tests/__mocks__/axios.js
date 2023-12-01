import "dotenv/config.js"

const mockHtmlData = 
`<html>
    <body>
        <title> This is a title </title>
        <p> Hello, World!</p>
        <p> This is a mock html data</p>
        <p> Bye </p>
    </body>
</html>`

const mockEmptyHtmlData = 
`<html>
    <body>
        <title> This is a title of an empty html </title>
    </body>
</html>`

const mockSearchNewsSuccessReturn =
{
    data:{
        value:[
            {
                "name": "test html",
                "url": "success_url",
                "datePublished": "2023-11-22T00:00:00.0000000",
                "provider":[{
                    "name": "test Publisher"
                }],
                "category":["test category", "General News"]
            }
        ]
    }
}

const mockSearchNewsSuccessReturn2 =
{
    data:{
        value:[
            {
                "name": "test html",
                "url": "success_url",
                "datePublished": "2023-11-22T00:00:00.0000000",
                "provider":[{
                    "name": "test Publisher"
                }]
            }
        ]
    }
}

const mockSearchNewsEmptyReturn =
{
    data:{
        value:[
            {
                "name": "empty html",
                "url": "empty_url",
                "datePublished": "2023-11-22T00:00:00.0000000",
                "provider":[{
                    "name": "empty publisher"
                }],
                "category":"empty category"
            }
        ]
    }
}
const mockSearchNewsErrReturn =
{
    data:{
        value:[
            {
                "name": "error html",
                "url": "err_url",
                "datePublished": "2023-11-22T00:00:00.0000000",
                "provider":[{
                    "name": "error publisher"
                }],
                "category":"error category"
            }
        ]
    }
}
const mockSummaryReturn = {
    data:{
        status:"succeeded",
        tasks:
        {
            items:[
                {
                    results:{
                        documents:[{
                            summaries:[{text:"summary success"}]

                        }]
                    }
                }
            ]
        }
    }
}

class mockAxios {
    static get (url, config) {
        if (config){
            if (config.headers["Ocp-Apim-Subscription-Key"] === "BingKey"){
                const user_query = config.params.q
                var msg = user_query.split(" ")
                if (msg[0] === "error"){
                    return Promise.resolve(mockSearchNewsErrReturn)
                }
                else if (msg[0] == "empty"){
                    return Promise.resolve(mockSearchNewsEmptyReturn)
                }
                else{
                    if (msg[0] == "1"){
                        return Promise.resolve(mockSearchNewsSuccessReturn)
                    }
                    else{
                        return Promise.resolve(mockSearchNewsSuccessReturn2)
                    }
                }
                
            }
            else if (config.headers["Ocp-Apim-Subscription-Key"] === "LangServiceKey"){
                return Promise.resolve(mockSummaryReturn)
                
            }
        }
        // else {
            if (url === "err_url"){
                return Promise.reject({
                    status:400,
                    para: ["error content"]
                })
            }
            else if (url == "empty_url"){
                return Promise.resolve({
                    status:200,
                    data:mockEmptyHtmlData
                })
            }
            return Promise.resolve({
                status:200,
                data:mockHtmlData
            })
        // }
    }

    static post(url,data, config){
        if (data.analysisInput.documents[0].text == "error content"){
            throw new Error("Error in Summary")
        }
        else{
            return Promise.resolve({
                headers:{
                    "operation-location": "success url"
                }
            })
        }
    }    
}
export default mockAxios;