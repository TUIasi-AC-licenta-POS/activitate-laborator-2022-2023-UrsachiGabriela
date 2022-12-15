package com.spotify.playlists.view.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExceptionResponse {
    String title;
    Integer status;
    String details;

}
