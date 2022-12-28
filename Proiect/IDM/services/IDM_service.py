import collections

import spyne
from munch import DefaultMunch
from spyne import Application, rpc, ServiceBase, String, Boolean, Array, Iterable
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication

from models.dto.authorize_response import AuthorizeResponse
from models.dto.role_dto import RoleDTO
from models.dto.user_dto import UserDTO
from repositories.role_repository import *
from repositories.user_repository import *
from utils import security
from utils.password_ops import encode, match, is_valid
from utils.roles_code import Roles


class IDMService(ServiceBase):

    # la crearea unui cont, utilizatorul poate sa isi selecteze tipul de cont (client/artist)
    @rpc(String, String, _returns=String)
    def register_user(ctx, uname, upass, urole):
        role = get_role_by_name(urole)

        if role not in [Roles.CLIENT.name, Roles.ARTIST.name]:
            raise spyne.Fault(faultcode='Client', faultstring="Invalid role")

        if not is_valid(upass):
            raise spyne.Fault(faultcode='Client', faultstring="Too weak password")

        encoded_password = encode(upass)
        create = create_user(uname, encoded_password, role)

        print(create)
        if create is True:
            return "Successfully created"

        if "users_UK" in create:
            raise spyne.Fault(faultcode='Client', faultstring="This username already exists")

        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

    # content manager created by admin
    @rpc(String, String, String, _returns=String)
    def create_user(ctx, access_token, uname, upass):
        auth_response: AuthorizeResponse = ctx.authorize(access_token)

        if Roles.APP_ADMIN.name not in auth_response.roles:
            raise spyne.Fault(faultcode='Client', faultstring="Invalid role")

        if not is_valid(upass):
            raise spyne.Fault(faultcode='Client', faultstring="Too weak password")

        encoded_password = encode(upass)
        role = get_role_by_name(Roles.CONTENT_MANAGER.name)
        create = create_user(uname, encoded_password, role)

        if create is True:
            return "Successfully created"

        if "users_UK" in create:
            raise spyne.Fault(faultcode='Client', faultstring="This username already exists")

        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

    @rpc(String, String, String, String, _returns=String)
    def change_upass(ctx, access_token, uname, old_pass, new_pass):
        auth_response: AuthorizeResponse = ctx.authorize(access_token)
        roles = auth_response.roles

        

        user = get_user_by_name(uname)

        if not match(old_pass, user.upass):
            raise spyne.Fault(faultcode='Client', faultstring="Incorrect old password")

        if old_pass == new_pass:
            raise spyne.Fault(faultcode='Client', faultstring="New password cannot be the same as old password")

        if update_password(user, encode(new_pass)) is True:
            return "Password successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not change the password for given user")

    @rpc(String, String, String, _returns=String)
    def add_user_role(ctx, access_token, uname, new_role):
        user = get_user_by_name(uname)
        role = get_role_by_name(new_role)  # to verify if new role name is valid

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if role is None:
            raise spyne.Fault(faultcode='Client', faultstring="This role doesn't exist")

        if update_user_roles(user, role) is True:
            return "Successfully updated"

        raise spyne.Fault(faultcode='Server', faultstring="Could not update roles for given user")

    @rpc(String, String, String, _returns=String)
    def remove_user_role(ctx, access_token, uname, removed_role):
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

    @rpc(String, String, _returns=String)
    def remove_user(ctx, access_token, uname):
        user = get_user_by_name(uname)

        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        if delete_user(user) is True:
            return "Successfully deleted"

        raise spyne.Fault(faultcode='Server', faultstring="Could not remove roles for given user")

    @rpc(String, String, _returns=UserDTO)
    def get_user_info(ctx, access_token, uname):
        user = get_user_by_name(uname)
        if user is None:
            raise spyne.Fault(faultcode='Client', faultstring="This user doesn't exist")

        roles = []
        for role in user.roles:
            dto = RoleDTO(role.rid, role.rname)
            roles.append(dto)

        result = UserDTO(user.uid, user.uname, roles)
        return result

    @rpc(String, _returns=Array(UserDTO))
    def list_users(ctx, access_token):
        users = get_users()
        result = []

        for user in users:
            roles = []
            for role in user.roles:
                dto = RoleDTO(role.rid, role.rname)
                roles.append(dto)

            result.append(UserDTO(user.uid, user.uname, roles))

        return result

    @rpc(String, _returns=Iterable(RoleDTO))
    def list_roles(ctx, access_token):
        roles = get_roles()
        result = []

        for role in roles:
            dto = RoleDTO(role.rid, role.rname)
            result.append(dto)

        return result

    @rpc(String, String, String, _returns=String)
    def login(ctx, access_token, uname, upass):
        user = get_user_by_name(uname)
        roles_name = list(map(lambda role: role.rname, user.roles))

        if user is not None:
            password = user.upass
            if match(upass, password):
                return security.create_access_token(user.uid, roles_name)
        return "False"

    @rpc(String, _returns=AuthorizeResponse)
    def authorize(ctx, access_token):
        # validate integrity + expiry date
        response = security.validate_token(access_token)
        if not response:
            raise spyne.Fault(faultcode='Client', faultstring="Invalid token")

        # validate roles
        obj = DefaultMunch.fromDict(response)
        if not ctx.verify_roles(obj.sub, obj.roles):
            raise spyne.Fault(faultcode='Client', faultstring="Invalid token")

        return AuthorizeResponse(obj.sub, obj.roles)

    @rpc(String, _returns=Boolean)
    def logout(ctx, access_token):
        security.add_to_blacklist(access_token)
        return True

    def verify_roles(ctx, uid, uroles):
        user = get_user_by_id(uid)

        if user is None:
            return False

        roles = user.roles

        uroles.sort()
        roles.sort()

        if collections.Counter(uroles) == collections.Counter(roles):
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
