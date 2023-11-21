import {jest} from '@jest/globals'
class MockedOAuth2Client {
    verifyIdToken (token, callback) {
      // Custom implementation for verifyIdToken
      if (token.idToken == "valid_token"){
        console.log("resolved")
        const payload = {
            sub:"4", 
            email: 'user4@gmail.com', 
            name:"user4"
        };
        const login = {
          getPayload: () => payload,
        };
        callback(null, login)
      }
      else if (token.idToken == "invalid_token"){
        console.log("reject")
        callback(null, null)
      }
      else{
        callback(new Error("error token"), null)
      }
    }
}
  
// Mocked module export
export const OAuth2Client = MockedOAuth2Client; // Export the mock directly

