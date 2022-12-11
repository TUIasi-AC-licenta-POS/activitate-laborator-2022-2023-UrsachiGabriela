package spotify.model.repos;

// https://stackoverflow.com/questions/12274019/how-to-configure-mongodb-collection-name-for-a-class-in-spring-data

import org.springframework.stereotype.Repository;


public interface CustomRepository {
    String getCollectionName();

    void setCollectionName(String newCollectionName);
}
