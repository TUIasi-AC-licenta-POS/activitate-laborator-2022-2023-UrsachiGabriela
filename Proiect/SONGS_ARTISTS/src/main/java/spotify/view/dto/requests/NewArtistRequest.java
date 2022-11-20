package spotify.view.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "artist")
@Relation(collectionRelation = "artists")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewArtistRequest{
    @NotBlank(message = "Artist name should not be empty")
    private String name;
    private Boolean active;
}