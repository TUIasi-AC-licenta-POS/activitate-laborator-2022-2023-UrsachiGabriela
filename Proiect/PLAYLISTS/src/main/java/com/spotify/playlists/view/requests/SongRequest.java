package com.spotify.playlists.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "song")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongRequest extends RepresentationModel<SongRequest> {
    private Integer id;
    private String name;
}