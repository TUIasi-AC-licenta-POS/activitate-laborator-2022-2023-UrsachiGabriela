package com.spotify.playlists.services.mappers;

import com.spotify.playlists.model.collections.Resource;
import com.spotify.playlists.view.requests.SongRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);


    default Resource toSongResource(SongRequest songRequest){
        if ( songRequest == null ) {
            return null;
        }

        Resource resource = new Resource();

        resource.setResourceId( songRequest.getId() );
        resource.setName( songRequest.getName() );
        resource.setLink(songRequest.getLink("self").get().getHref());

        return resource;
    }
}
