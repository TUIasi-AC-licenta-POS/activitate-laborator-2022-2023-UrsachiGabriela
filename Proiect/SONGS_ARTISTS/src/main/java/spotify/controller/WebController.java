package spotify.controller;

import com.spotify.idmclient.wsdl.AuthorizeResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.IDMClient;
import spotify.configs.IDMClientConfig;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.services.authorization.AuthService;
import spotify.services.dataprocessors.ArtistsService;
import spotify.services.dataprocessors.SongsService;
import spotify.services.dtoassemblers.ArtistModelAssembler;
import spotify.services.dtoassemblers.SongModelAssembler;
import spotify.services.mappers.ArtistMapper;
import spotify.services.mappers.SongMapper;
import spotify.utils.enums.UserRoles;
import spotify.view.requests.NewArtistRequest;
import spotify.view.requests.NewSongRequest;
import spotify.view.requests.NewSongsForArtistRequest;
import spotify.view.responses.ArtistResponse;
import spotify.view.responses.ExceptionResponse;
import spotify.view.responses.SongResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


//TODO
// do not assign song to an inactive artist
// logs
// modify model assembler
// uuid with type String
// modify regex for names

@Log4j2
@RestController
@Validated
public class WebController {

    @Autowired
    private AuthService authService;
    @Autowired
    private ArtistsService artistsService;
    @Autowired
    private SongsService songsService;
    @Autowired
    private ArtistModelAssembler artistModelAssembler;
    @Autowired
    private SongModelAssembler songModelAssembler;

    private final ArtistMapper artistMapper = ArtistMapper.INSTANCE;
    private final SongMapper songMapper = SongMapper.INSTANCE;

    @Autowired
    private PagedResourcesAssembler<SongResponse> songDTOPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<ArtistResponse> artistDTOPagedResourcesAssembler;


//    @GetMapping("/api/songcollection/artists")
//    public ResponseEntity<CollectionModel<ArtistDTO>> getAllArtists() {
//        // get entities
//        Set<ArtistEntity> artistEntities = artistsService.getAllArtists();
//
//        // map to data transfer objects
//        Set<ArtistDTO> artistDTOS = artistMapper.toArtistDTOWithoutSongsSet(artistEntities);
//
//        // add links
//        CollectionModel<ArtistDTO> artistModels = artistModelAssembler.toCollectionModel(artistDTOS);
//
//        return ResponseEntity.ok().body(artistModels);
//    }

    @Operation(summary = "Get all artists")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched artists", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @GetMapping(value = "/api/songcollection/artists")
    public ResponseEntity<PagedModel<ArtistResponse>> getAllArtists(
            @RequestParam(required = false)
            @Min(0) Integer page,

            @RequestParam(defaultValue = "1", required = false)
            @Min(value = 1, message = "Page size must not be less than one ")
            Integer size,

            @RequestParam(required = false)
            @Pattern(regexp = "^[a-zA-Z\\\\s]*", message = "Invalid name format")
            String name,

            @Pattern(regexp = "exact", message = "Invalid match")
            @RequestParam(required = false)
            String match
    ) {
        log.info("[{}] -> GET, getAllArtists", this.getClass().getSimpleName());

        // query db
        Page<ArtistEntity> artistEntities = artistsService.getPageableArtists(page, size, name, match);

        // map to dto
        Page<ArtistResponse> artistDTOPage = artistEntities.map(artistMapper::toArtistWithoutSongsDto);

        // add links
        PagedModel<ArtistResponse> artistModels = artistDTOPagedResourcesAssembler.toModel(artistDTOPage, artistModelAssembler);

        return ResponseEntity.ok().body(artistModels);
    }


    /**
     * @param uuid id-ul artistului cautat
     * @return Proprietatile artistului + link catre piesele sale
     */
    @Operation(summary = "Get artist by its unique identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched artist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)
            })
    @GetMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable int uuid) {
        log.info("[{}] -> GET, getArtistById, id:{}", this.getClass().getSimpleName(),uuid);

        // query database
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);

        // map to dto
        ArtistResponse artistResponse = artistMapper.toArtistWithoutSongsDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

        return ResponseEntity.ok().body(artistResponse);
    }

    @Operation(summary = "Get songs for artist identified by uuid")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found artist", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping("/api/songcollection/artists/{uuid}/songs")
    public ResponseEntity<ArtistResponse> getAllSongsForGivenArtist(@PathVariable int uuid) {
        log.info("[{}] -> GET, getAllSongsForGivenArtist, artistId:{}", this.getClass().getSimpleName(),uuid);

        // query db
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);

        // map to dto
        ArtistResponse artistResponse = artistMapper.toArtistWithSongsDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);
        for (SongResponse songResponse : artistResponse.getSongs()) {
            songModelAssembler.toSimpleModel(songResponse);
        }

        return ResponseEntity.ok().body(artistResponse);
    }

    @Operation(summary = "Get artists for song identified by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found song by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping("/api/songcollection/songs/{id}/artists")
    public ResponseEntity<Set<ArtistResponse>> getAllArtistsForGivenSong(@PathVariable int id) {
        log.info("[{}] -> GET, getAllArtistsForGivenSong, songId:{}", this.getClass().getSimpleName(),id);

        // query db
        Set<ArtistEntity> artistEntities = artistsService.getArtistForGivenSong(id);

        // map to dto
        Set<ArtistResponse> artistResponses = artistMapper.toArtistWithName(artistEntities);

        // add links
        artistResponses.forEach(artistDTO -> artistModelAssembler.toSimpleModel(artistDTO));

        return ResponseEntity.ok().body(artistResponses);
    }

    @Operation(summary = "Create new artist or replace an existing one")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully created a new artist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "204", description = "Successfully replaced an existing resource with given uuid", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict: unique name constraint unsatisfied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),
            })
    @PutMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistResponse> createNewArtist(@PathVariable int uuid,
                                                          @Valid @RequestBody NewArtistRequest newArtist,
                                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        log.info("[{}] -> PUT, createOrReplaceArtist, uuid:{}, artist:{}", this.getClass().getSimpleName(),uuid,newArtist);

        // authorize
        authService.authorize(authorizationHeader,UserRoles.CONTENT_MANAGER);

        // query db to decide which of create or replace operation is needed
        boolean isAlreadyExistent = artistsService.itExistsArtist(uuid);

        // map dto to entity
        ArtistEntity artistEntity = artistMapper.toArtistEntity(newArtist,uuid);

        // update db
        ArtistEntity savedEntity = artistsService.createOrReplaceArtist(artistEntity);

        // map created/replaced entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(savedEntity);

        // add links
        artistModelAssembler.toComplexModel(artistResponse);

        // decide response code
        if (isAlreadyExistent)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(artistResponse);
    }

    @Operation(summary = "Delete  artist identified by uuid")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted artist resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched artist not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @DeleteMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistResponse> deleteArtist(@PathVariable int uuid,
                                                       @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> DELETE, deleteArtist, uuid:{}", this.getClass().getSimpleName(),uuid);

        // authorize
        authService.authorize(authorizationHeader,UserRoles.CONTENT_MANAGER);

        // query db for an entity with given identifier
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);

        // map deleted entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

        // delete entity
        artistsService.deleteArtist(artistEntity); // Se vor sterge si intrarile in tabela de join care corespund artistului dat, dar piesele raman

        return ResponseEntity.status(HttpStatus.OK).body(artistResponse); // reprezentarea resursei inainte de a fi stearsa
    }

    // fie adaug songs pentru un artist dupa ce am introdus song-ul
    // fie specific la adaugarea unui song lista de artisti -> addNewSong
    @Operation(summary = "Assign songs to an artist")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated join table", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ArtistResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched artist/song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),

            })
    @PostMapping("/api/songcollection/artists/{uuid}/songs")
    public ResponseEntity<ArtistResponse> assignSongsToArtist(@PathVariable int uuid,
                                                              @Valid @RequestBody NewSongsForArtistRequest request,
                                                              @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> POST, assignSongsToArtist, uuid:{}, songs:{}", this.getClass().getSimpleName(),uuid,request);

        // authorize
        authService.authorize(authorizationHeader,UserRoles.CONTENT_MANAGER);

        // query db for songs and artist
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);
        Set<SongEntity> songEntities = new HashSet<>();
        for (Integer songId : request.getSongsId()) {
            songEntities.add(songsService.getSongById(songId));
        }

        // update db
        ArtistEntity updatedArtist = artistsService.addSongsToArtist(artistEntity, songEntities);

        // map entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(updatedArtist);

        // add links
        artistModelAssembler.toComplexModel(artistResponse);
        for(SongResponse songResponse:artistResponse.getSongs()){
            songModelAssembler.toSimpleModel(songResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(artistResponse);
    }

    /**
     * @return O lista cu toate piesele din colectie (info complete) -> 200 Ok
     */
    @Operation(summary = "Get all songs")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched songs", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content),
                    @ApiResponse(responseCode = "422", description = "Unable to process the contained instructions", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))}),

            })
    @GetMapping("/api/songcollection/songs")
    public ResponseEntity<PagedModel<SongResponse>> getAllSongs(
            @RequestParam(required = false)
            @Min(0) Integer page,

            @RequestParam(defaultValue = "1", required = false)
            @Min(value = 1, message = "Page size should not be less than one ")
            Integer size,

            @RequestParam(required = false)
            @Pattern(regexp = "title|year|genre", message = "Invalid criteria")
            String searchBy,

            @RequestParam(required = false)
            String searchedValue,

            @Pattern(regexp = "exact", message = "Invalid match")
            @RequestParam(required = false)
            String match

    ) {
        log.info("[{}] -> GET, getAllSongs", this.getClass().getSimpleName());

        // query database
        Page<SongEntity> songEntities = songsService.getPageableSongs2(page,size,searchBy,searchedValue,match);

        // map entities to dtos
        Page<SongResponse> songDTOPage = songEntities.map(songMapper::toCompleteSongDto);

        // add links
        for (SongResponse songResponse : songDTOPage) {
            songResponse.getSongs().forEach(s -> songModelAssembler.toSimpleModel(s));
        }
        PagedModel<SongResponse> songModels = songDTOPagedResourcesAssembler.toModel(songDTOPage, songModelAssembler);

        return ResponseEntity.ok().body(songModels);
    }

    /**
     * @param id id-ul piesei cautate
     * @return daca exista => piesa cautata + 200 ok
     * daca nu exista => 404 Not found
     */
    @Operation(summary = "Get song by its identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched song", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for path variables", content = @Content)

            })
    @GetMapping("/api/songcollection/songs/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable int id) {
        log.info("[{}] -> GET, getSongById, songId:{}", this.getClass().getSimpleName(),id);

        // query database
        SongEntity songEntity = songsService.getSongById(id);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(songEntity);

        // add links
        songModelAssembler.toModel(songResponse);
        for (SongResponse innerSong : songResponse.getSongs()) {
            songModelAssembler.toSimpleModel(innerSong);
        }

        return ResponseEntity.ok().body(songResponse);
    }


    @Operation(summary = "Add new song to song resources")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully added  new song resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Malformed request syntax", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Mentioned artists not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Mentioned album doesn't exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Semantically erroneous request body fields", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),

            })
    @PostMapping("/api/songcollection/songs")
    public ResponseEntity<SongResponse> addNewSong(@Valid @RequestBody NewSongRequest newSong,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> POST, addNewSong, song:{}", this.getClass().getSimpleName(),newSong);

        // authorize
        authService.authorize(authorizationHeader, UserRoles.CONTENT_MANAGER);

        // query db for album and artists
        SongEntity album = newSong.getParentId() != null ? songsService.getAlbumById(newSong.getParentId()) : null;
        Set<ArtistEntity> artistEntities = artistsService.getArtistsByName(newSong.getArtists());

        // map dto to entity
        SongEntity songEntity = songMapper.toSongEntity(newSong, album);

        // db insertion and create entry in join table
        SongEntity createdEntity = songsService.createNewSong(songEntity);
        artistsService.assignSongToMultipleArtists(artistEntities,songEntity);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(createdEntity);

        // add links
        songModelAssembler.toComplexModel(songResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(songResponse);
    }

    @Operation(summary = "Delete song resource identified by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted song resource", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: cannot remove album without removing all its songs", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @DeleteMapping("/api/songcollection/songs/{id}")
    public ResponseEntity<SongResponse> deleteSong(@PathVariable int id,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        log.info("[{}] -> DELETE, deleteSong, songId:{}", this.getClass().getSimpleName(),id);

        // authorize
        authService.authorize(authorizationHeader,UserRoles.CONTENT_MANAGER);

        // query db
        SongEntity songEntity = songsService.getSongById(id);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(songEntity);

        // add links
        songModelAssembler.toModel(songResponse);

        // delete song from songs table and also from join table
        songsService.deleteSong(songEntity);

        return ResponseEntity.status(HttpStatus.OK).body(songResponse);
    }

}
