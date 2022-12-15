import bcrypt

def encode(password):
    pass_bytes = password.encode('utf-8')
    salt = bcrypt.gensalt()

    hash_pass = bcrypt.hashpw(pass_bytes, salt)

    return hash_pass


def match(password, hash_password):
    return bcrypt.checkpw(password.encode('utf-8'), hash_password.encode('utf-8'))

