package spotify.services.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import spotify.model.entities.SongEntity;
import spotify.view.requests.NewSongRequest;
import spotify.view.responses.SongDTO;

import java.util.Set;

@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

    @Named("completeSong")
    @Mapping(source = "songEntity.parent.id", target = "parentId")
    @Mapping(source = "songEntity.songEntities", target = "songs", qualifiedByName = "simpleSongDtoSet")
    SongDTO toCompleteSongDto(SongEntity songEntity);

    @Named("simpleSong")
    default SongDTO toSimpleSongDto(SongEntity songEntity) {
        return SongDTO.builder()
                .id(songEntity.getId())
                .name(songEntity.getName())
                .genre(songEntity.getGenre())
                .build();
    }

    @Mapping(source = "newSongRequest.parentId", target = "parent.id")
    default SongEntity toSongEntity(NewSongRequest newSongRequest, SongEntity album) {
        SongEntity songEntity = new SongEntity();

        songEntity.setName(newSongRequest.getName());
        songEntity.setGenre(newSongRequest.getGenre());
        if (newSongRequest.getYear() != null) {
            songEntity.setYear(newSongRequest.getYear());
        }

        songEntity.setType(newSongRequest.getType());
        songEntity.setParent(album);

        return songEntity;
    }

    @IterableMapping(qualifiedByName = "completeSong")
    Set<SongDTO> toCompleteSongDTOSet(Set<SongEntity> songEntities);

    @Named("simpleSongDtoSet")
    @IterableMapping(qualifiedByName = "simpleSong")
    Set<SongDTO> toSimpleSongDTOSet(Set<SongEntity> songEntities);
}
