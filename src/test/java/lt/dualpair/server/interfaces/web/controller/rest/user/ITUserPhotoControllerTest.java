package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("userTest_photos.xml")
public class ITUserPhotoControllerTest extends BaseRestControllerTest {

    @Test
    public void testDeleteUserPhoto_forbidden() throws Exception {
        mockMvc.perform(delete("/api/user/1/photos/1").with(bearerToken(2L)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    public void testDeleteUserPhoto_invalidCount() throws Exception {
        mockMvc.perform(delete("/api/user/1/photos/1").with(bearerToken(1L)))
                .andExpect(status().isConflict())
                .andExpect(content().string(new Contains("User must have at least one photo")));
        assertEquals((Long)1L, jdbcTemplate.queryForObject("select count(*) from user_photos where id=1 and user_id=1", Long.class));
    }

    @Test
    public void testDeleteUserPhoto() throws Exception {
        assertEquals((Long)2L, jdbcTemplate.queryForObject("select count(*) from user_photos where user_id=2", Long.class));
        mockMvc.perform(delete("/api/user/2/photos/2").with(bearerToken(2L)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        flushPersistenceContext();
        assertEquals((Long)1L, jdbcTemplate.queryForObject("select count(*) from user_photos where user_id=2", Long.class));
        assertEquals((Long)0L, jdbcTemplate.queryForObject("select count(*) from user_photos where id=1 and user_id=2", Long.class));
    }

    @Test
    public void testAddUserPhoto_forbidden() throws Exception {
        mockMvc.perform(put("/api/user/1/photos")
                .with(bearerToken(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountType\": \"FB\", \"idOnAccount\": \"3\", \"sourceUrl\": \"c\"}"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(""));
    }

    @Test
    public void testAddUserPhoto() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/user/1/photos")
                .with(bearerToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountType\": \"FB\", \"idOnAccount\": \"3\", \"sourceUrl\": \"c\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();
        PhotoResource photoResource = new ObjectMapper().readValue(result.getResponse().getContentAsByteArray(), PhotoResource.class);
        assertEquals((Long)4L, photoResource.getPhotoId());
        assertEquals((Long)2L, jdbcTemplate.queryForObject("select count(*) from user_photos where user_id=1", Long.class));

    }
}