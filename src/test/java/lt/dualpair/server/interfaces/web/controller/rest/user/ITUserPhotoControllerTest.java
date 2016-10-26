package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
}