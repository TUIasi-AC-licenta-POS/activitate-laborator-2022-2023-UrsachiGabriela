import spyne
from spyne import Application, rpc, ServiceBase, String, Boolean, Array, Iterable
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication

from models.dto.role_dto import RoleDTO
from models.dto.user_dto import UserDTO
from repositories import user_repository
from repositories.role_repository import *
from repositories.user_repository import *
from utils.password_encoder import encode, match
from utils.password_validator import is_valid
from utils.register_data import RegisterRequest, RegisterResponse


class IDMService(ServiceBase):
    @rpc(RegisterRequest, _returns=String)
    def create_user(ctx, request):
        default_role = get_role_by_name("guest")

        if not is_valid(request.upass):
            raise spyne.Fault(faultcode='Client', faultstring="Too weak password")

        encoded_password = encode(request.upass)
        create = create_user(request.uname, encoded_password, default_role)

        print(create)
        if create is True:
            return "Successfully created"

        if "users_UK" in create:
            raise spyne.Fault(faultcode='Client', faultstring="This username already exists")

        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

    @rpc(String, String, String, _returns=String)
    def change_upass(ctx, uname, old_pass, new_pass):
        user = get_user_by_name(uname)

        if not match(old_pass, user.upass):
            raise spyne.Fault(faultcode='Client', faultstring="Incorrect old password")

        if old_pass == new_pass:
            raise spyne.Fault(faultcode='Client', faultstring="New password cannot be the same as old password")

        if update_password(user, encode(new_pass)) is True:
            return "Password successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not change the password for given user")

    @rpc(String, String, _returns=String)
    def add_user_role(ctx, uname, new_role):
        user = get_user_by_name(uname)
        role = get_role_by_name(new_role)  # to verify if new role name is valid

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        if update_user_roles(user, role) is True:
            return "Successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not update roles for given user")

    @rpc(String, String, _returns=String)
    def remove_user_role(ctx, uname, removed_role):
        user = get_user_by_name(uname)
        role = get_role_by_name(removed_role)  # to verify if new role name is valid

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        delete_status = delete_user_role(user, role)
        if delete_status is True:
            return "Successfully deleted"

        if "not in list" in delete_status:
            raise spyne.Fault(faultcode='Client', faultstring="The role has not been assigned to this user")

        raise spyne.Fault(faultcode='Server', faultstring="Could not remove roles for given user")

    @rpc(String, _returns=String)
    def remove_user(ctx, uname):
        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if delete_user(user) is True:
            return "Successfully deleted"

        raise spyne.Fault(faultcode='Server', faultstring="Could not remove roles for given user")

    @rpc(String, _returns=UserDTO)
    def get_user_info(ctx, uname):
        user = get_user_by_name(uname)

        roles = []
        for role in user.roles:
            dto = RoleDTO(role.rid, role.rname)
            roles.append(dto)

        result = UserDTO(user.uid, user.uname, roles)
        return result

    @rpc(_returns=Array(UserDTO))
    def list_users(ctx):
        users = get_users()
        result = []

        for user in users:
            roles = []
            for role in user.roles:
                dto = RoleDTO(role.rid, role.rname)
                roles.append(dto)

            result.append(UserDTO(user.uid, user.uname, roles))

        return result

    @rpc(_returns=Iterable(RoleDTO))
    def list_roles(ctx):
        roles = get_roles()
        result = []

        for role in roles:
            dto = RoleDTO(role.rid, role.rname)
            result.append(dto)

        return result

    @rpc(String, String, _returns=Boolean)
    def login(ctx, uname, upass):
        user = get_user_by_name(uname)

        if user is not None:
            password = user.upass
            if match(upass, password):
                return True
        return False
    
    @rpc(String, String, _returns=Boolean)
    def authorize(ctx, uname, urole):
        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        role = get_role_by_name(urole)
        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        for r in user.roles:
            if r.rname == urole:
                return True
        return False


application = Application([IDMService], 'services.IDM.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()
