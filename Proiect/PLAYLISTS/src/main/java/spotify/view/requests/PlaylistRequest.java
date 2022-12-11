package spotify.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "playlists")
@Relation(collectionRelation = "playlists")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistRequest {
    //@NotBlank(message = "Playlist name should not be empty")
    @Pattern(regexp = "\\b([a-zA-ZÀ-ÿ][-,a-z. ']+[ ]*)+", message = "Invalid name format")
    private String name;
}
