from spyne import ComplexModel, Integer, String,Array


class AuthorizeResponse(ComplexModel):
    sub = Integer()
    roles = Array(String)

    def __init__(self,sub,roles):
        super(AuthorizeResponse,self).__init__()
        self.sub =sub
        self.roles=roles
