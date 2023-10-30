
const  endpoint = "https://quicknewslangservice.cognitiveservices.azure.com/language/analyze-text/jobs?api-version=2023-04-01";
const port = 8000;
const host = "127.0.0.1";
// const axios = require('axios');
// const express = require('express');
import axios from "axios";
import express from "express"
let app = express();
const key = "13f7ddc06c5b4661bcd8a9f61acf6198";


let sampleText = `At Microsoft, we have been on a quest to advance AI beyond existing techniques, by taking a more holistic, human-centric approach to learning and understanding. As Chief Technology Officer of Azure AI services, I have been working with a team of amazing scientists and engineers to turn this quest into a reality. In my role, I enjoy a unique perspective in viewing the relationship among three attributes of human cognition: monolingual text (X), audio or visual sensory signals, (Y) and multilingual (Z). At the intersection of all three, there’s magic—what we call XYZ-code as illustrated in Figure 1—a joint representation to create more powerful AI that can speak, hear, see, and understand humans better. We believe XYZ-code will enable us to fulfill our long-term vision: cross-domain transfer learning, spanning modalities and languages. The goal is to have pre-trained models that can jointly learn representations to support a broad range of downstream AI tasks, much in the way humans do today. Over the past five years, we have achieved human performance on benchmarks in conversational speech recognition, machine translation, conversational question answering, machine reading comprehension, and image captioning. These five breakthroughs provided us with strong signals toward our more ambitious aspiration to produce a leap in AI capabilities, achieving multi-sensory and multilingual learning that is closer in line with how humans learn and understand. I believe the joint XYZ-code is a foundational component of this aspiration, if grounded with external knowledge sources in the downstream AI tasks.`;

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





   export const summarizeArticle = async function(text){
        try{    
            let data = sumReqBody;
                
            data.analysisInput.documents[0].text = text;
    

            const response = await axios.post(endpoint, data,
                {
                    headers :{
                        "Ocp-Apim-Subscription-Key" : key
                    }
                })

            let path = await response.headers["operation-location"];
            console.log(path);
            var summary;
            
            do{
                await sleep(1000);
                summary = await getSummary(path);
                console.log(summary.status);
            }while(summary.status !== "succeeded" );
            
            return summary.tasks.items[0].results.documents[0].summaries[0].text;
    
        }catch(err){
            console.log(err);
        }
    }


// TESTING
app.listen(port, async () =>  {
	console.log(`${new Date()}  ForumDB communcation. Listening on ${host}:${port}`);
    //getSummary();
    //summarizeArticle(sampleText).then((res) =>{console.log(res)});
    console.log(await summarizeArticle(sampleText));
    // var sm = new SummarizationEngine();
    // console.log(await sm.summarizeArticle(sampleText));

});





var getSummary = async function(path){
    const response = await axios.get(path,
     {
        headers: {
        "Ocp-Apim-Subscription-Key" : key
        }
    });
    return response.data;
};




function sleep(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}



// class SummarizationEngine {
//     construtor(){
//         this.dateCreated = new Date();
//     }

//     summarizeArticle = async function(text){
//         try{    
//             let data = sumReqBody;
                
//             data.analysisInput.documents[0].text = text;
    

//             const response = await axios.post(endpoint, data,
//                 {
//                     headers :{
//                         "Ocp-Apim-Subscription-Key" : key
//                     }
//                 })

//             let path = await response.headers["operation-location"];
//             console.log(path);
//             var summary;
            
//             do{
//                 await sleep(1000);
//                 summary = await getSummary(path);
//                 console.log(summary.status);
//             }while(summary.status !== "succeeded" );
            
//             return summary.tasks.items[0].results.documents[0].summaries[0].text;
    
//         }catch(err){
//             console.log(err);
//         }
//     }

// }

//module.exports = SummarizationEngine;