package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import spotify.model.collections.Resource;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "playlist")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistResponse extends RepresentationModel<PlaylistResponse> {
    private String id;
    private String name;
    private Set<Resource> favSongs;
}
