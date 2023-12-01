class MockJwt{
    static sign(payload, key, options){
        return payload.id
    }

    static verify(token, key){
        if (token === "expired"){
            throw new Error("Expired Token")
        }
        // else{
            var returnVal = new Object()
            returnVal.id = token
            return returnVal
        // }
    }
}

export default MockJwt;