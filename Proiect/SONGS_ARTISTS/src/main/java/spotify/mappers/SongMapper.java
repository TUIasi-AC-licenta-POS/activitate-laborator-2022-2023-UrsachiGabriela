package spotify.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import spotify.model.entities.SongEntity;
import spotify.view.dto.responses.SongDTO;

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

    @IterableMapping(qualifiedByName = "completeSong")
    Set<SongDTO> toCompleteSongDTOSet(Set<SongEntity> songEntities);

    @Named("simpleSongDtoSet")
    @IterableMapping(qualifiedByName = "simpleSong")
    Set<SongDTO> toSimpleSongDTOSet(Set<SongEntity> songEntities);
}
