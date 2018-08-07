package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("userTest_photos.xml")
public class ITUserPhotoControllerTest extends BaseRestControllerTest {

    @Test
    @Ignore // TODO update mock mvc version and implement test
    public void testSetUserPhotos_forbidden() throws Exception {

        MockMultipartFile file = new MockMultipartFile("photoFiles", "0", "image/jpg", "image".getBytes());
        MockMultipartFile json = new MockMultipartFile("data", "", "application/json", "{\"json\": \"someValue\"}".getBytes());

        mockMvc.perform(post("/api/user/1/photos")
                .with(bearerToken(2L))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content("{\"accountType\": \"FB\", \"idOnAccount\": \"3\", \"sourceUrl\": \"c\"}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @Ignore // TODO update mock mvc version and implement test
    public void testSetUserPhotos() throws Exception {
        List<PhotoResource> photoResourceList = Arrays.asList(createPhotoResource(0, "fakeurl1"),
                createPhotoResource(1, "fakeurl2"));

        mockMvc.perform(post("/api/user/1/photos")
                    .with(bearerToken(1L))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .content(new ObjectMapper().writeValueAsString(photoResourceList)))
                .andExpect(status().isOk())
                .andReturn();

        flushPersistenceContext();

        assertEquals((Long)2L, jdbcTemplate.queryForObject("select count(*) from user_photos where user_id=1", Long.class));
        List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from user_photos where user_id=1 order by position asc");
        assertRow(result.get(0), "FB", "idOnAccount1", 0, "url1");
        assertRow(result.get(1), "FB", "idOnAccount2", 1, "url2");
    }

    private void assertRow(Map<String, Object> row, String accountType, String idOnAccount, int position, String url) {
        assertEquals(accountType, row.get("account_type"));
        assertEquals(idOnAccount, row.get("id_on_account"));
        assertEquals(position, row.get("position"));
        assertEquals(url, row.get("source_link"));
    }

    private PhotoResource createPhotoResource(int position, String url) {
        PhotoResource photoResource = new PhotoResource();
        photoResource.setPosition(position);
        photoResource.setSource(url);
        return photoResource;
    }
}