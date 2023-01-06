package spotify.view.requests;

import lombok.*;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import javax.validation.Valid;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewSongRequest {

    private String name;


    private MusicGenre genre;


    private Integer year;


    private MusicType type;

    private @Valid Integer parentId;


    private Set<String> artists;

}
