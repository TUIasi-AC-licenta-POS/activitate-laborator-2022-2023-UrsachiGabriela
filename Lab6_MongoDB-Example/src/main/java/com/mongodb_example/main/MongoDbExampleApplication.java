package com.mongodb_example.main;

import com.mongodb_example.main.repositories.SteamUserRepository;
import com.mongodb_example.main.steam_users.models.Game;
import com.mongodb_example.main.steam_users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class MongoDbExampleApplication implements CommandLineRunner {

	@Autowired
	SteamUserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(MongoDbExampleApplication.class, args);
	}

	public String getIdByUsername(String username){
		return userRepository.findUserByUsername(username).getId();
	}
	public void showAllUsers(){
		userRepository.findAll().forEach(user -> System.out.println(user.toString()));
	}

	public void createAUser(){
		List<String> achievements = List.of("New Here", "10 Games");
		List<Game> games = List.of(new Game("Dirt",0), new Game("The Witcher III",0));
		User user = new User("Ratonu", 2018, achievements, games);
		userRepository.save(user);

	}

	public void updateUser(String username){
		User user = userRepository.findUserByUsername(username);
		user.getAchievements().add("New Achievement");
		userRepository.save(user);
	}

	public void removeUser(String id){
		userRepository.deleteById(id);
	}

	@Override
	public void run(String... args) throws Exception {
		createAUser();
		System.out.println("All users");
		showAllUsers();
		removeUser(getIdByUsername("Ratonu"));
		System.out.println("Users after removal");
		showAllUsers();
		createAUser();
		updateUser("Ratonu");
		System.out.println("Users after add and update");
		showAllUsers();
	}
}
