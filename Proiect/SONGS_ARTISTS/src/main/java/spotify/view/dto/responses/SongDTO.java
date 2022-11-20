package spotify.view.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import spotify.model.entities.enums.MusicGenre;
import spotify.model.entities.enums.MusicType;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "song")
@Relation(collectionRelation = "songs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongDTO extends RepresentationModel<SongDTO> {
    private Integer id;
    private String name;
    private MusicGenre genre;
    private Integer year;
    private MusicType type;
    private Integer parentId;
    private Set<SongDTO> songs;
}
