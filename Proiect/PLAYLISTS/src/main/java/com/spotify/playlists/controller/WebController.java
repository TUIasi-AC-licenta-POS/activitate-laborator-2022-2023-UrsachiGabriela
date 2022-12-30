package com.spotify.playlists.controller;

import com.spotify.playlists.IDMClient;
import com.spotify.playlists.IDMClientConfig;
import com.spotify.playlists.model.collections.Playlist;
import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.services.PlaylistService;
import com.spotify.playlists.services.clients.RestClient;
import com.spotify.playlists.services.dtoassemblers.PlaylistModelAssembler;
import com.spotify.playlists.services.mappers.PlaylistMapper;
import com.spotify.playlists.services.mappers.SongMapper;
import com.spotify.playlists.view.SongRequestResponse;
import com.spotify.playlists.view.requests.PlaylistRequest;
import com.spotify.playlists.view.requests.SimpleSongRequest;
import com.spotify.playlists.view.responses.ExceptionResponse;
import com.spotify.playlists.view.responses.PlaylistResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/api/playlistscollection")
public class WebController {

    // temporary
    private static final String USER_ID = "1";

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RestClient restClient;

    private final PlaylistMapper playlistMapper = PlaylistMapper.INSTANCE;
    private final SongMapper songMapper = SongMapper.INSTANCE;

    @Autowired
    private PlaylistModelAssembler playlistModelAssembler;

    @Operation(summary = "Get all playlists")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found playlists", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @GetMapping(value = "/playlists")
    public ResponseEntity<CollectionModel<PlaylistResponse>> getAllPlaylists(
            @RequestParam(required = false)
            @Pattern(regexp = "^[-,a-zA-Z0-9\\s]*", message = "Invalid name format")
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

    @Operation(summary = "Get playlist by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched playlist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
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

    @Operation(summary = "Add new playlist to playlist collection")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully added  new playlist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
                    @ApiResponse(responseCode = "409", description = "Conflict: unique name constraint unsatisfied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
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

//    @Operation(summary = "Add songs to an existing playlist - from SA module")
//    @ApiResponses(value =
//            {
//                    @ApiResponse(responseCode = "200", description = "Successfully inserted new song in playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
//                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
//                    @ApiResponse(responseCode = "404", description = "Searched playlist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
//                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
//            })
//    @PostMapping("/playlists/{id}/songs")
//    public ResponseEntity<PlaylistResponse> addSongToPlaylist(@PathVariable String id, @RequestBody SongRequestResponse songRequestResponse) {
//        // map to resource
//        Resource song = songMapper.toSongResource(songRequestResponse);
//
//        // update db
//        Playlist updatedPlaylist = playlistService.addSongToPlaylist(USER_ID, id, song);
//
//        // map to dto
//        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(updatedPlaylist);
//
//        // add links
//        playlistModelAssembler.toModel(playlistResponse);
//
//        return ResponseEntity.ok().body(playlistResponse);
//    }

    @Operation(summary = "Add songs to an existing playlist")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "204", description = "Successfully inserted new song in playlist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PlaylistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched playlist not found or given song not existent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @PatchMapping("/playlists/{id}/songs")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(@PathVariable String playlistId, @Validated @RequestBody SimpleSongRequest songRequest) {
        // send request
        SongRequestResponse songRequestResponse = restClient.getSongById(songRequest.getSongId());

        // map to resource
        Resource song = songMapper.toSongResource(songRequestResponse);

        // update db
        Playlist updatedPlaylist = playlistService.addSongToPlaylist(USER_ID, playlistId, song);

        // map to dto
        PlaylistResponse playlistResponse = playlistMapper.toPlaylistDTO(updatedPlaylist);

        // add links
        playlistModelAssembler.toModel(playlistResponse);

        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/1")
    public ResponseEntity<String> getUserId() {
        IDMClient idmClientService = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);

        //idmClientService.setDefaultUri("http://127.0.0.1:8000");

        String name = idmClientService.getUserInfoResponse("Ana");
        return ResponseEntity.ok().body(name);

    }
}
