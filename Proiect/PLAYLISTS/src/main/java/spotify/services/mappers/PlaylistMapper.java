package spotify.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import spotify.model.collections.Playlist;
import spotify.view.requests.PlaylistRequest;
import spotify.view.responses.PlaylistResponse;

import java.util.Set;


@Mapper(uses = {SongMapper.class})
public interface PlaylistMapper {
    PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

    PlaylistResponse toPlaylistDTO(Playlist playlist);

    Playlist toPlaylist(PlaylistResponse playlistResponse);
    Playlist toPlaylist(PlaylistRequest playlistRequest);

    Set<PlaylistResponse> toPlaylistDTOSet(Set<Playlist> playlists);
}
