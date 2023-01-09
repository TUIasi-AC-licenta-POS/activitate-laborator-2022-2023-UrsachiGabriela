package spotify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
import java.util.Set;


//TODO
// logs
// uuid with type String
// update functionality
// add 401 and 403 status codes to openAPI description

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











}
