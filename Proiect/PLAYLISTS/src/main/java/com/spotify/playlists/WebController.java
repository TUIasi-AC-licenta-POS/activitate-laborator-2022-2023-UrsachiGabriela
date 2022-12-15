package com.spotify.playlists;

import com.spotify.idmclient.wsdl.GetUserInfoResponse;
import com.spotify.playlists.model.collections.Playlist;
import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.services.PlaylistService;
import com.spotify.playlists.services.dtoassemblers.PlaylistModelAssembler;
import com.spotify.playlists.services.mappers.PlaylistMapper;
import com.spotify.playlists.services.mappers.SongMapper;
import com.spotify.playlists.view.requests.PlaylistRequest;
import com.spotify.playlists.view.requests.SongRequest;
import com.spotify.playlists.view.responses.PlaylistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/api/playlistscollection")
public class WebController {

    // temporary
    private static final String USER_ID = "1";

    @Autowired
    private PlaylistService playlistService;

    private final PlaylistMapper playlistMapper = PlaylistMapper.INSTANCE;
    private final SongMapper songMapper = SongMapper.INSTANCE;

    @Autowired
    private PlaylistModelAssembler playlistModelAssembler;


    @GetMapping(value = "/playlistss")
    public ResponseEntity<Set<Playlist>> getAllPlaylists1() {
        List<Resource> songs = List.of(
                new Resource(12, "Fav song1", ""),
                new Resource(18, "Fav song2", ""),
                new Resource(19, "Fav song3", "")
        );

        Playlist playlist = new Playlist("First playlist");
        playlist.setFavSongs(songs);

        //playlistService.savePlaylist(playlist);
        playlistService.createPlaylist("1", playlist);
        // get collection
        Set<Playlist> playlists = playlistService.getAllPlaylists("1", "a");

        return ResponseEntity.ok().body(playlists);
    }

    @GetMapping(value = "/playlists")
    public ResponseEntity<CollectionModel<PlaylistResponse>> getAllPlaylists(
            @RequestParam(required = false)
            @Pattern(regexp = "\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+", message = "Invalid name format")
            String name
    ) {
        // get documents
        Set<Playlist> playlists = playlistService.getAllPlaylists(USER_ID, name);

        // map to dto
        Set<PlaylistResponse> playlistResponses = playlistMapper.toPlaylistDTOSet(playlists);

        // add links
        CollectionModel<PlaylistResponse> playlistModels = playlistModelAssembler.toCollectionModel(playlistResponses);

        return ResponseEntity.ok().body(playlistModels);
    }

    @GetMapping(value = "/playlists/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable String id) {
        // get document
        Playlist playlist = playlistService.getPlaylistById(USER_ID, id);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(playlist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.ok().body(playlistResponse);
    }

    @PostMapping("/playlists")
    public ResponseEntity<PlaylistResponse> createPlaylist(@Valid @RequestBody PlaylistRequest playlistRequest) {
        // map dto to document
        Playlist playlist = playlistMapper.toPlaylist(playlistRequest);

        // insert in db
        playlist = playlistService.createPlaylist(USER_ID, playlist);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(playlist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.ok().body(playlistResponse);
    }


    @PostMapping("/playlists/{id}")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(@PathVariable String id, @RequestBody SongRequest songRequest) {
        // map to resource
        Resource song = songMapper.toSongResource(songRequest);

        // update db
        Playlist updatedPlaylist = playlistService.addSongToPlaylist(USER_ID, id, song);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(updatedPlaylist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.ok().body(playlistResponse);
    }

//


//    @PostMapping("/{playlistName}")
//    public ResponseEntity<Playlist> addSongToPlaylist(@PathVariable String playlistName, @RequestBody Resource song){
//        Playlist updatedPlaylist = playlistService.addSongToPlaylist("1",playlistName,song);
//
//        return ResponseEntity.ok().body(updatedPlaylist);
//    }

    //    @GetMapping(value = "/playlistss")
//    public ResponseEntity<String> getUserId() {
//        IDMClient idmClient = new AnnotationConfigApplicationContext(SoapClientConfig.class).getBean(spotify.services.IDMClient.class);
//        GetUserInfoResponse response = idmClient.getUserInfo("Ana");
//
//        return ResponseEntity.ok().body(response.toString());
//    }


    @GetMapping(value = "/1")
    public ResponseEntity<String> getUserId() {
        IDMClient idmClientService = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);

        //idmClientService.setDefaultUri("http://127.0.0.1:8000");

        String name = idmClientService.getUserInfoResponse("Ana");
        return ResponseEntity.ok().body(name);

    }
}
