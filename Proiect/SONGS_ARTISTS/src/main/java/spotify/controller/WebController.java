package spotify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.IDMClient;
import spotify.IDMClientConfig;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.services.dataprocessors.ArtistsService;
import spotify.services.dataprocessors.SongsService;
import spotify.services.dtoassemblers.ArtistModelAssembler;
import spotify.services.dtoassemblers.SongModelAssembler;
import spotify.services.mappers.ArtistMapper;
import spotify.services.mappers.SongMapper;
import spotify.services.validators.CreateValidator;
import spotify.services.validators.FilterValidator;
import spotify.utils.enums.MusicGenre;
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
import java.util.Set;


//TODO
// do not assign song to an inactive artist


@RestController
@Validated
public class WebController {
    @Autowired
    private ArtistsService artistsService;

    @Autowired
    private SongsService songsService;

    @Autowired
    private ArtistModelAssembler artistModelAssembler;

    @Autowired
    private SongModelAssembler songModelAssembler;


    @Autowired
    private CreateValidator createValidator;

    @Autowired
    private FilterValidator filterValidator;

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
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content)

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
        // query db
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);

        // map to dto
        ArtistResponse artistResponse = artistMapper.toArtistWithSongsDto(artistEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

        for (SongResponse songResponse : artistResponse.getSongs()) {
            songResponse = songModelAssembler.toSimpleModel(songResponse);
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
        // query db
        SongEntity songEntity = songsService.getSongById(id);
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
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables or malformed request syntax", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflict: unique name constraint unsatisfied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            })
    @PutMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistResponse> createNewArtist(@PathVariable int uuid, @Valid @RequestBody NewArtistRequest newArtist) {
        // query db to decide which of create or replace operation is needed
        boolean isAlreadyExistent = artistsService.itExistsArtist(uuid);

        // map dto to entity
        ArtistEntity artistEntity = artistMapper.toArtistEntity(newArtist);
        artistEntity.setId(uuid); //in mapper nu se seteaza id-ul deoarece acesta e path variable si nu face parte din request body

        // insert in db
        ArtistEntity savedEntity = artistsService.createNewArtist(artistEntity);

        // map created/replaced entity to dto
        ArtistResponse artistResponse = artistMapper.toCompleteArtistDto(savedEntity);

        // add links
        artistModelAssembler.toModel(artistResponse);

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
    public ResponseEntity<ArtistResponse> deleteArtist(@PathVariable int uuid) {
        // query db for an entity with given identifier
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);

        // map entity to deleted dto
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
                    @ApiResponse(responseCode = "400", description = "Incorrect syntax for path variables or malformed request syntax", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Searched artist/song not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PostMapping("/api/songcollection/artists/{uuid}/songs")
    public ResponseEntity<ArtistResponse> assignSongsToArtist(@PathVariable int uuid, @Valid @RequestBody NewSongsForArtistRequest request) {
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
        artistModelAssembler.toModel(artistResponse);

        return ResponseEntity.status(HttpStatus.OK).body(artistResponse);
    }

    /**
     * @return O lista cu toate piesele din colectie (info complete) -> 200 Ok
     */
    @Operation(summary = "Get all songs")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found searched songs", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SongResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid syntax for query params", content = @Content)
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
        Page<SongEntity> songEntities;

        // query database
        if (searchBy == null || searchedValue == null) {
            // display all without conditions
            songEntities = songsService.getPageableSongs(page, size);

        } else {
            // validate type of searched value for given searchedBy param
            filterValidator.validate(searchedValue, searchBy);

            songEntities = searchBy.equals("title") ? (songsService.getPageableSongsByTitle(page, size, searchedValue, match))
                    : (searchBy.equals("year") ? songsService.getPageableSongsByYear(page, size, Integer.parseInt(searchedValue))
                    : songsService.getPageableSongsByGenre(page, size, MusicGenre.valueOf(searchedValue.toUpperCase())));

        }

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
                    @ApiResponse(responseCode = "404", description = "Mentioned album not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Semantically erroneous request body fields", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),

            })
    @PostMapping("/api/songcollection/songs")
    public ResponseEntity<SongResponse> addNewSong(@Valid @RequestBody NewSongRequest newSong) {
        // query db for given song and its album
        SongEntity album = newSong.getParentId() != null ? songsService.getSongById(newSong.getParentId()) : null;
        SongEntity songEntity = songMapper.toSongEntity(newSong, album);

        // validate request body
        createValidator.validate(songEntity);

        // db insertion and create entry in join table
        SongEntity createdEntity = songsService.createNewSong(songEntity);
        for (String artistName : newSong.getArtists()) {
            ArtistEntity artistEntity = artistsService.getArtistByName(artistName);
            artistsService.addSongsToArtist(artistEntity, new HashSet<>() {{
                add(songEntity);
            }});
        }

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(createdEntity);

        // add links
        songModelAssembler.toModel(songResponse);

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
    public ResponseEntity<SongResponse> deleteSong(@PathVariable int id) {
        // query db
        SongEntity songEntity = songsService.getSongById(id);

        // map entity to dto
        SongResponse songResponse = songMapper.toCompleteSongDto(songEntity);

        // add links
        songModelAssembler.toModel(songResponse);

        // delete song from songs table and also from join table
        artistsService.removeSongFromArtists(id);
        songsService.deleteSong(songEntity);

        return ResponseEntity.status(HttpStatus.OK).body(songResponse);
    }


    @GetMapping(value = "/1")
    public ResponseEntity<String> getUserId() {
        IDMClient idmClientService = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);

        //idmClientService.setDefaultUri("http://127.0.0.1:8000");

        String name = idmClientService.getUserInfoResponse("Ana");
        return ResponseEntity.ok().body(name);

    }
}
