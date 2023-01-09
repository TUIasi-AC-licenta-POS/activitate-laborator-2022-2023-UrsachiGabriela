package spotify.view.responses;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistResponse extends RepresentationModel<ArtistResponse> {
    private Integer id;

    @NotBlank(message = "Artist name should not be empty")
    private String name;

    private Boolean active;

    private Set<SongResponse> songs;

    private Boolean hasSongs = Boolean.FALSE;
}
