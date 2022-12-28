from models.entities.role_orm import Role
from repositories.repository import Repository
from utils.roles_code import Roles

session = Repository.get_session()

def get_roles():
    roles = session.query(Role).all()
    return roles


def get_role_by_name(rname):
    role = session.query(Role).filter(Role.rname == rname)
    return role.first()


