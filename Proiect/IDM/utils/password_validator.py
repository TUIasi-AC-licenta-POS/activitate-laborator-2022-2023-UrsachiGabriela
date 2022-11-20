import re

password_pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"


def is_valid(password):
    if re.match(password_pattern, password):
        return True

    return False
