package spotify.model.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import spotify.model.collections.Playlist;

import java.util.List;
import java.util.Optional;


public interface PlaylistRepository extends MongoRepository<Playlist, String>, CustomRepository {
    boolean existsByName(String name);

    Optional<Playlist> findByName(String name);

    List<Playlist> findAllByNameContaining(String name);
}
