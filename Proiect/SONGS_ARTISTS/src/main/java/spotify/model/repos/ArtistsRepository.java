package spotify.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import spotify.model.entities.ArtistEntity;

public interface ArtistsRepository extends JpaRepository<ArtistEntity, Integer> {
   // List<ArtistEntity> findArtistsBySongsId(int songId);
}
