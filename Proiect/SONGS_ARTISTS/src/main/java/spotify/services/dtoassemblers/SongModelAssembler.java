package spotify.services.dtoassemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import spotify.controller.WebController;
import spotify.view.responses.SongDTO;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SongModelAssembler extends RepresentationModelAssemblerSupport<SongDTO, SongDTO> {
    public SongModelAssembler() {
        super(WebController.class, SongDTO.class);
    }

    @Override
    public SongDTO toModel(SongDTO songDTO) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(
                methodOn(WebController.class).getAllSongs(null, null, null, null, null))
                .withRel("parent"));

        songDTO = toSimpleModel(songDTO);

        links.add(linkTo(methodOn(WebController.class).deleteSong(songDTO.getId())).withRel("delete song").withType("DELETE"));
        songDTO.add(links);

        return songDTO;
    }

    public SongDTO toSimpleModel(SongDTO songDTO) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(WebController.class)
                .getSongById(songDTO.getId()))
                .withSelfRel());

        if (songDTO.getParentId() != null) {
            links.add(linkTo(methodOn(WebController.class)
                    .getSongById(songDTO.getParentId()))
                    .withRel("album"));
        }

        songDTO.add(links);

        return songDTO;
    }

    @Override
    public CollectionModel<SongDTO> toCollectionModel(Iterable<? extends SongDTO> songDTOS) {
        CollectionModel<SongDTO> newSongDTOS = super.toCollectionModel(songDTOS);
        newSongDTOS.add(linkTo(methodOn(WebController.class).getAllSongs(null, null, null, null, null)).withSelfRel());

        return newSongDTOS;
    }
}
