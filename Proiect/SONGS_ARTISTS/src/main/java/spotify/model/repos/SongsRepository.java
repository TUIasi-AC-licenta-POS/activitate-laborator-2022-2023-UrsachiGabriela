package spotify.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spotify.model.entities.SongEntity;

import java.util.List;

public interface SongsRepository extends JpaRepository<SongEntity, Integer> {
    @Query("select sa from songsAlbums sa where sa.parent=2")
    List<SongEntity> findByParent(int parent);
}
