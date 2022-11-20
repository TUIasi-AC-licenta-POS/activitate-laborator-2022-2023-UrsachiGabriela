from models.role_orm import Role
from base.sql_base import Session


def get_roles():
    session = Session()
    roles = session.query(Role).all()
    return roles
