package spotify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spotify.mappers.ArtistMapper;
import spotify.mappers.SongMapper;
import spotify.model.entities.ArtistEntity;
import spotify.model.entities.SongEntity;
import spotify.services.ArtistsService;
import spotify.services.SongsService;
import spotify.view.assemblers.ArtistModelAssembler;
import spotify.view.assemblers.SongModelAssembler;
import spotify.view.dto.requests.NewArtistRequest;
import spotify.view.dto.requests.NewSongsForArtistRequest;
import spotify.view.dto.responses.ArtistDTO;
import spotify.view.dto.responses.SongDTO;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

//TODO
// response code for POST request when nothing is modified

@RestController
public class WebController {
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

    @GetMapping("/api/songcollection/artists")
    public ResponseEntity<CollectionModel<ArtistDTO>> getAllArtists() {
        Set<ArtistEntity> artistEntities = artistsService.getAllArtists();
        Set<ArtistDTO> artistDTOS = artistMapper.toArtistDTOWithoutSongsSet(artistEntities);
        CollectionModel<ArtistDTO> artistModels = artistModelAssembler.toCollectionModel(artistDTOS);

        return ResponseEntity.ok().body(artistModels);
    }

    @GetMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable int uuid) {
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);
        ArtistDTO artistDTO = artistMapper.toArtistWithoutSongsDto(artistEntity);
        artistDTO = artistModelAssembler.toModel(artistDTO);

        return ResponseEntity.ok().body(artistDTO);
    }

    @GetMapping("/api/songcollection/artists/{uuid}/songs")
    public ResponseEntity<ArtistDTO> getAllSongsForGivenArtist(@PathVariable int uuid) {
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);
        ArtistDTO artistDTO = artistMapper.toArtistWithSongsDto(artistEntity);
        artistDTO = artistModelAssembler.toModel(artistDTO);

        for (SongDTO songDto : artistDTO.getSongs()) {
            songDto = songModelAssembler.toSimpleModel(songDto);
        }

        return ResponseEntity.ok().body(artistDTO);
    }

    @PutMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistDTO> createNewArtist(@PathVariable int uuid, @Valid @RequestBody NewArtistRequest newArtist) {
        boolean wasAlreadyExistent = false;

        ArtistEntity artistEntity = artistMapper.toArtistEntity(newArtist);
        artistEntity.setId(uuid);

        if (artistsService.itExistsArtist(uuid)) {
            wasAlreadyExistent = true;
        }

        ArtistEntity savedEntity = artistsService.createNewArtist(artistEntity);
        ArtistDTO artistDTO = artistMapper.toCompleteArtistDto(savedEntity);
        artistDTO = artistModelAssembler.toModel(artistDTO);

        if (wasAlreadyExistent)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(artistDTO);
    }

    @DeleteMapping("/api/songcollection/artists/{uuid}")
    public ResponseEntity<ArtistDTO> deleteArtist(@PathVariable int uuid) {
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);
        ArtistDTO artistDTO = artistMapper.toCompleteArtistDto(artistEntity);
        artistDTO = artistModelAssembler.toModel(artistDTO);

        artistsService.deleteArtist(artistEntity);

        return ResponseEntity.status(HttpStatus.OK).body(artistDTO);
    }

    @PostMapping("/api/songcollection/artists/{uuid}/songs")
    public ResponseEntity<ArtistDTO> assignSongsToArtist(@PathVariable int uuid, @Valid @RequestBody NewSongsForArtistRequest request) {
        ArtistEntity artistEntity = artistsService.getArtistById(uuid);
        Set<SongEntity> songEntities = new HashSet<>();

        for (Integer songId : request.getSongsId()) {
            songEntities.add(songsService.getSongById(songId));
        }

        ArtistEntity updatedArtist = artistsService.addSongsToArtist(artistEntity, songEntities);
        ArtistDTO artistDTO = artistMapper.toCompleteArtistDto(updatedArtist);
        artistDTO = artistModelAssembler.toModel(artistDTO);

        return ResponseEntity.status(HttpStatus.OK).body(artistDTO);
    }

    @GetMapping("/api/songcollection/songs")
    public ResponseEntity<CollectionModel<SongDTO>> getAllSongs() {
        Set<SongEntity> songEntities = songsService.getAllSongs();
        Set<SongDTO> songDTOS = songMapper.toCompleteSongDTOSet(songEntities);
        CollectionModel<SongDTO> songModels = songModelAssembler.toCollectionModel(songDTOS);

        for (SongDTO songDto : songModels) {
            songDto.getSongs().forEach(s -> songModelAssembler.toSimpleModel(s));
        }

        return ResponseEntity.ok().body(songModels);
    }


    @GetMapping("/api/songcollection/songs/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable int id) {
        SongEntity songEntity = songsService.getSongById(id);
        SongDTO songDTO = songMapper.toCompleteSongDto(songEntity);
        songDTO = songModelAssembler.toModel(songDTO);

        for (SongDTO innerSong : songDTO.getSongs()) {
            innerSong = songModelAssembler.toSimpleModel(innerSong);
        }

        return ResponseEntity.ok().body(songDTO);
    }

}
