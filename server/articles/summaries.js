
const  endpoint = "https://quicknewslangservice.cognitiveservices.azure.com/language/analyze-text/jobs?api-version=2023-04-01";
const port = 8000;
const host = "127.0.0.1";
// const axios = require('axios');
// const express = require('express');
import axios from "axios";
import express from "express"
let app = express();
const key = "13f7ddc06c5b4661bcd8a9f61acf6198";


let sampleText = `HONG KONG, Oct 30 (Reuters) - A hearing on Monday to wind up China Evergrande Group (3333.HK) was adjourned until Dec. 4, with a Hong Kong High Court judge giving the world's most indebted property developer one last chance to come up with a restructuring plan or face liquidation. Evergrande, which has more than $300 billion of liabilities including $19 billion in international market bonds, defaulted on its offshore debt in late 2021 and became the poster child of a debt crisis that has since engulfed China's property sector. Justice Linda Chan told the court the next hearing would be the last before a decision is made on the winding up order. Evergrande needed to come up with a ‘concrete’ revised restructuring proposal before that date, she said, otherwise it was likely the firm would be wound up. A liquidator could still negotiate with creditors on a restructuring and make progress on a deal being reached, Chan said. The company's shares trimmed losses to 8% after the adjournment on Monday, having fallen as much as 23% in the morning session. 'NO BETTER OPTION' China's property sector accounts for about a quarter of the activity in the world's second-largest economy. Its woes have rattled global markets and prompted a slew of measures by Beijing to reassure investors and homeowners. A liquidation of Evergrande, which listed total assets of $240 billion as at end-June, would send further shockwaves through already fragile capital markets, but is expected to have little immediate impact on the company's operations, including its many home construction projects. ‘I don’t think anyone wants to see it liquidated. But right now, we don't see a better option could be offered by Evergrande, so the chance is still high that it would be wound up eventually,’ said an Evergrande bondholder, asking to be unnamed because they were not authorised to speak with the media. Evergrande did not respond to request for comment. Evergrande had been working on an offshore debt restructuring plan but the plan was thrown off course last month when its billionaire founder Hui Ka Yan was confirmed to be under investigation for suspected criminal activities. Evergrande grew rapidly through a land-buying spree backed by loans and by selling apartments quickly at low margins, making Hui Asia's richest man in 2017, according to Forbes. But with its overall liabilities ballooning, Evergrande came under increasing pressure as the property market weakened and Chinese regulators cracked down on companies with high debt levels. Top Shine, an investor in Evergrande unit Fangchebao, filed the winding-up petition in June 2022 because it said Evergrande had not honoured an agreement to repurchase shares the investor bought in the unit. Evergrande revealed the investigation into its founder and one of its main subsidiaries last month, and it was barred by mainland regulators from issuing new dollar bonds, a crucial part of the restructuring plan. It also cancelled creditor votes originally scheduled for late last month. Fellow property developer Logan Group's (3380.HK) winding up order was also adjourned by the same court to Dec 4. The Shenzhen-based company said last year it would suspend interest payments and restructure its offshore debt including $3.7 billion in dollar bonds due to liquidity pressure. Little progress has been made on the restructuring talks since the company said in March it started negotiations with offshore creditors to agree with proposed restructuring terms, bondholders told Reuters. Logan did not immediately respond to request for comment. Logan and two of its subsidiaries received a winding-up petition in Nov 2022 filed by the bond trustee who represents a few investors holding the 5.75% 2025 bond.`;





   export const summarizeArticle = async function(text, sentenceCount){
        try{    
            let data = sumReqBody;
            data.analysisInput.documents[0].text = text;
            data.tasks[0].parameters.sentenceCount = sentenceCount;

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
    //console.log(await summarizeArticle(sampleText, 5));
    // var sm = new SummarizationEngine();
    // console.log(await sm.summarizeArticle(sampleText));
    //console.log(sampleText);

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
