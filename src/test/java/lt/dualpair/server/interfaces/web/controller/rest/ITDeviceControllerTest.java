package lt.dualpair.server.interfaces.web.controller.rest;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("deviceTest.xml")
public class ITDeviceControllerTest extends BaseRestControllerTest {

    @Test
    public void testRegisterDevice() throws Exception {
        mockMvc.perform(post("/api/device?id=123").with(bearerToken(1L)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
        flushPersistenceContext();
        Map<String, Object> devices = jdbcTemplate.queryForMap("select * from user_devices where user_id=1");
        assertFalse(devices.isEmpty());
        assertEquals("123", devices.get("id"));
    }

    @Test
    public void testRegisterDevice_exists() throws Exception {
        mockMvc.perform(post("/api/device?id=600").with(bearerToken(2L)))
                .andExpect(status().isConflict())
                .andExpect(content().string(""));
        flushPersistenceContext();
        // checks if only one device exists, will throw exception if not
        Map<String, Object> devices = jdbcTemplate.queryForMap("select * from user_devices where user_id=2");
        assertFalse(devices.isEmpty());
    }
}