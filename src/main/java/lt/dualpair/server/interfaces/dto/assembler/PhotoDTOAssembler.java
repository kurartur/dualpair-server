package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.interfaces.dto.PhotoDTO;
import org.springframework.stereotype.Component;

@Component
public class PhotoDTOAssembler extends DTOAssembler<Photo, PhotoDTO> {

    @Override
    public Photo toEntity(PhotoDTO photoDTO) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public PhotoDTO toDTO(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(photo.getId());
        dto.setSourceLink(photo.getSourceLink());
        return dto;
    }
}
