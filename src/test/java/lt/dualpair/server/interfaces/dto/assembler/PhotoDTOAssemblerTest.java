package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.interfaces.dto.PhotoDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhotoDTOAssemblerTest {

    private PhotoDTOAssembler assembler = new PhotoDTOAssembler();

    @Test
    public void testToDTO() throws Exception {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setSourceLink("http://photo1");
        PhotoDTO dto = assembler.toDTO(photo);
        assertEquals((Long)1L, dto.getId());
        assertEquals("http://photo1", dto.getSourceLink());
    }
}