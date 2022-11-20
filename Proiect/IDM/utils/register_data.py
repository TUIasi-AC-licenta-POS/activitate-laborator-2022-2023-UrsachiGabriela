from spyne import ComplexModel, String, Integer, Boolean


class RegisterRequest(ComplexModel):
    uname = String()
    upass = String()


class RegisterResponse(ComplexModel):
    message = String()
    statusCode = Integer()
    # pot sa pun status code?
