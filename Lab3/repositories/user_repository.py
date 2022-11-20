from models.user_orm import User
from base.sql_base import Session


def get_users():
    session = Session()
    users = session.query(User).all()
    return users


def create_user(username, password):
    session = Session()
    user = User(username, password)
    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        print(f"Failed to add user - {exc}")
    return user
