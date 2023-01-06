package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

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

    private String name;

    private Boolean active;

    private Set<SongResponse> songs;

    private Boolean hasSongs = Boolean.FALSE;
}
