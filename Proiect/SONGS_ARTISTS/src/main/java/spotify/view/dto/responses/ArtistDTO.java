package spotify.view.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "artist")
@Relation(collectionRelation = "artists")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistDTO extends RepresentationModel<ArtistDTO> {
    private Integer id;

    @NotBlank(message = "Artist name should not be empty")
    private String name;
    private Boolean active;
    private Set<SongDTO> songs;
}
