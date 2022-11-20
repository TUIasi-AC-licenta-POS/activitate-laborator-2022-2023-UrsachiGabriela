from sqlalchemy import Column, String, Integer, Table, ForeignKey
from base.sql_base import Base

# movies_actors_association = Table(
#     'users_roles', Base.metadata,
#     Column('user_id', Integer, ForeignKey('users.id')),
#     Column('role_id', Integer, ForeignKey('roles.id'))
# )


class Role(Base):
    __tablename__ = 'roles'

    rid = Column(Integer, primary_key=True)
    rname = Column(String)

    def __init__(self, value):
        self.rname = value
