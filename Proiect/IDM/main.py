# python3 -m pip install mariadb sqlalchemy
from models.entities.role_orm import Role
from repositories.role_repository import get_roles
from repositories.user_repository import get_users, create_user

if __name__ == "__main__":
    print("Creating user:")
    #new_user = create_user("test", "test",Role("guest"))
    #print(new_user)

    print("\nUsers:")
    for user in get_users():
        print(f"{user.uid} - {user.uname} - {user.upass} - roles: ", end="")
        for role in user.roles:
            print(f"{role.rname} ", end="")
        print()

    print("\n\nRoles:")
    for role in get_roles():
        print(f"{role.rname}")
