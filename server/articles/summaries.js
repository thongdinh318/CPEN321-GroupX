import axios from "axios";
import "dotenv/config.js";
const  endpoint = "https://quicknewsummarymodule.cognitiveservices.azure.com/language/analyze-text/jobs?api-version=2023-04-01";
var key =""
if (process.env.LangServiceKey == undefined){
  key = "LangServiceKey";

}
else{
  key = process.env.LangServiceKey
}
const sumReqBody = {
  "displayName": "Document ext Summarization Task Example",
  "analysisInput": {
    "documents": [
      {
        "id": "1",
        "language": "en",
        "text": "example lmao"
      }
    ]
  },
  "tasks": [
    {
      "kind": "AbstractiveSummarization",
      "taskName": "Document Abstractive Summarization Task 1",
      "parameters": {
        "sentenceCount": 6
      }
    }
  ]
};

// ChatGPT usage: No.
export const summarizeArticle = async function(text, sentenceCount){
  let data = sumReqBody;
  data.analysisInput.documents[0].text = text;
  data.tasks[0].parameters.sentenceCount = sentenceCount;
  try{    
    const apiKeyHeader = {"Ocp-Apim-Subscription-Key" : key} 

    const response = await axios.post(endpoint, data,
        {
            headers:apiKeyHeader
        })

    let path = await response.headers["operation-location"];
    var summary;
    
    do{
        await sleep(1000);
        summary = await getSummary(path);
    }while(summary.status !== "succeeded" );
    return summary.tasks.items[0].results.documents[0].summaries[0].text;

  }catch(err){
      return err
  }
}

// ChatGPT usage: No.
var getSummary = async function(path){
  const apiKeyHeader = {"Ocp-Apim-Subscription-Key" : key} 
  const response = await axios.get(path,
   {
      headers: apiKeyHeader
  });
  return response.data;
};

// ChatGPT usage: No.
function sleep(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}
