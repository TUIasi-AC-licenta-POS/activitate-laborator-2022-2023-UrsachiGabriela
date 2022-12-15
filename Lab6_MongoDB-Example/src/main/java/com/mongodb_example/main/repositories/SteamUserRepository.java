package com.mongodb_example.main.repositories;

import com.mongodb_example.main.steam_users.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SteamUserRepository extends MongoRepository<User, String> {
    

    @Query("{username:'?0'}")
    User findUserByUsername(String username);

}
