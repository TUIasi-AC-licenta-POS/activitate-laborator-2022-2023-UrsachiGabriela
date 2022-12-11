package spotify.model.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "#{@playlistRepository.getCollectionName()}")
public class Playlist {
    @Id
    private String id;
    private String name;
    private List<Resource> favSongs;

    public Playlist(String name) {
        super();
        this.name = name;
    }
}
