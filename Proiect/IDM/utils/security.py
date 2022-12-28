import json
import uuid
from datetime import datetime, timedelta

from jwcrypto import jwk, jws
from jwcrypto.common import json_encode

ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 10
SECRET_KEY = jwk.JWK.generate(kty='oct', size=256)

blacklist=[]

def get_expiration_time(expires_delta: timedelta):
    if expires_delta:
        expire = datetime.now() + expires_delta
    else:
        expire = datetime.now() + timedelta(minutes=15)

    return expire


def create_access_token(userId: int, userRoles: [],
                        expires_delta: timedelta | None = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)):
    claims = {}
    claims["iss"] = "http://127.0.0.1:8000"
    claims["sub"] = userId
    claims["exp"] = get_expiration_time(expires_delta).isoformat()
    claims["jti"] = str(uuid.uuid1())
    claims["roles"] = userRoles

    jwt = json.dumps(claims)
    header = {"alg": ALGORITHM, "typ": "JWT"}
    # header=json.dumps(header)

    jws_token = jws.JWS(payload=jwt.encode('utf-8'))
    jws_token.add_signature(SECRET_KEY, protected=json_encode(header))

    return jws_token.serialize(compact=True)


def validate_token(access_token: str):

    try:
        j = jws.JWS()
        j.deserialize(access_token)
    except:
        return False
    # verify if token is already blacklisted
    if is_in_blacklist(access_token):
        return False

    # validate integrity
    try:
        j.verify(SECRET_KEY, ALGORITHM)
    except:
        add_to_blacklist(access_token)
        return False


    # verify expiry_date
    payload = json.loads(j.payload)
    if (datetime.fromisoformat(payload['exp']) < datetime.now()):
        add_to_blacklist(access_token)
        return False

    user_roles= {'sub':payload["sub"],'roles': payload["roles"]}
    return user_roles



def add_to_blacklist(token:str):
    blacklist.append(token)

def is_in_blacklist(token:str):
    return token in blacklist



if __name__ == '__main__':
    access_token = create_access_token(userId=1, userRoles=[1, 2])

    print(validate_token(access_token))
    # print(jws.JWS.from_jose_token(access_token))
    # JWS(payload={"exp":"2022-12-18T21:32:53.143974","iss":"http://127.0.0.1:8000","jti":"568d77a6-7f17-11ed-90fb-098a8f8eff17","roles":[1],"sub":1})

    # print("ACCESS TOKEN: "+access_token)
    # eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiAiaHR0cDovLzEyNy4wLjAuMTo4MDAwIiwgInN1YiI6IDEsICJleHAiOiAiMjAyMi0xMi0yOFQxNzoxNTo1NS4zNzI3MTMiLCAianRpIjogIjIwYjI2ZmNjLTg2YzEtMTFlZC04NzIzLTYzNTliZTRlMTUzNCIsICJyb2xlcyI6IFsxXX0.mvE4aay6N-5p5aZ5pMcfIIPYzwDtzqN6glmq0DinL3g

    # print(json.loads(j.payload.decode('utf-8'))["sub"])
