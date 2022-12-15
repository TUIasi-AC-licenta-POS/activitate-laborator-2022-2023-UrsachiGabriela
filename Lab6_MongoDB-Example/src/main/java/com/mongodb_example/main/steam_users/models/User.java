package com.mongodb_example.main.steam_users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "steam_users")
public class User {
    @Id
    private String id;
    private String username;
    private Integer join_year;
    private List<String> achievements;
    private List<Game> games;

    public User(String username, Integer join_year, List<String> achievements, List<Game> games) {
        super();
        this.username = username;
        this.join_year = join_year;
        this.achievements = achievements;
        this.games = games;
    }
}
