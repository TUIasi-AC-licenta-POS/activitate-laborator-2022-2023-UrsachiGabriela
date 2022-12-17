package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.WebController;
import spotify.view.responses.SongResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SongModelAssembler extends RepresentationModelAssemblerSupport<SongResponse, SongResponse> {
    public SongModelAssembler() {
        super(WebController.class, SongResponse.class);
    }

    @Override
    public SongResponse toModel(SongResponse songResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(
                methodOn(WebController.class).getAllSongs(null, null, null, null, null))
                .withRel("parent"));

        songResponse = toSimpleModel(songResponse);

        links.add(linkTo(methodOn(WebController.class).deleteSong(songResponse.getId())).withRel("delete song").withType("DELETE"));
        songResponse.add(links);

        return songResponse;
    }

    public SongResponse toSimpleModel(SongResponse songResponse) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(WebController.class)
                .getSongById(songResponse.getId()))
                .withSelfRel());

        if (songResponse.getParentId() != null) {
            links.add(linkTo(methodOn(WebController.class)
                    .getSongById(songResponse.getParentId()))
                    .withRel("album"));
        }

        songResponse.add(links);

        return songResponse;
    }

    @Override
    public CollectionModel<SongResponse> toCollectionModel(Iterable<? extends SongResponse> songDTOS) {
        CollectionModel<SongResponse> newSongDTOS = super.toCollectionModel(songDTOS);
        newSongDTOS.add(linkTo(methodOn(WebController.class).getAllSongs(null, null, null, null, null)).withSelfRel());

        return newSongDTOS;
    }
}
