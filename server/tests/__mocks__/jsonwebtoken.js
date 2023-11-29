class MockJwt{
    static sign(payload, key, options){
        return payload.id
    }

    static verify(token, key){
        if (token === "expired"){
            throw new Error("Expired Token")
        }
        else{
            return{id: token}
        }
    }
}

export default MockJwt;