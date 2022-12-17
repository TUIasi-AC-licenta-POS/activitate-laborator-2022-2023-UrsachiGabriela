package spotify.view.requests;

import lombok.*;
import spotify.utils.enums.MusicGenre;
import spotify.utils.enums.MusicType;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NewSongRequest {
    @NotBlank(message = "Song name should not be empty")
    private String name;

    @NotNull(message = "Music genre should not be null")
    private MusicGenre genre;

    @Min(value = 0, message = "Invalid year")
    @Max(value = 9999, message = "Invalid year")
    private Integer year;

    @NotNull(message = "Music type should not be null")
    private MusicType type;

    private @Valid Integer parentId;

    @NotNull(message = "Artists list should not be empty")
    private Set<String> artists;

}
