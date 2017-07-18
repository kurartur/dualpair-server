package lt.dualpair.server.interfaces.web.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.photo.PhotoTestUtils;
import lt.dualpair.core.user.UserAccount;
import lt.dualpair.server.interfaces.resource.user.PhotoResource;
import lt.dualpair.server.interfaces.web.controller.rest.BaseRestControllerTest;
import lt.dualpair.server.service.user.MockSocialDataProviderFactory;
import lt.dualpair.server.service.user.SocialDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DatabaseSetup("userTest_photos.xml")
public class ITUserPhotoControllerTest extends BaseRestControllerTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SocialDataProvider socialDataProvider = mock(SocialDataProvider.class);
        MockSocialDataProviderFactory.setSocialDataProvider(UserAccount.Type.FACEBOOK, socialDataProvider);
        Photo photo1 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount1", "url1");
        Photo photo2 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount2", "url2");
        Photo photo3 = PhotoTestUtils.createPhoto(UserAccount.Type.FACEBOOK, "idOnAccount3", "url3");
        when(socialDataProvider.getPhoto("idOnAccount1")).thenReturn(Optional.of(photo1));
        when(socialDataProvider.getPhoto("idOnAccount2")).thenReturn(Optional.of(photo2));
        when(socialDataProvider.getPhoto("idOnAccount3")).thenReturn(Optional.of(photo3));
        when(socialDataProvider.getPhoto("idOnAccount4")).thenReturn(Optional.empty());
        when(socialDataProvider.getPhotos(any(List.class))).thenAnswer(new Answer<List>() {
            @Override
            public List answer(InvocationOnMock invocation) throws Throwable {
                List ids = (List)invocation.getArguments()[0];
                List<Photo> result = new ArrayList<>();
                if (ids.contains("idOnAccount1")) result.add(photo1);
                if (ids.contains("idOnAccount2")) result.add(photo2);
                if (ids.contains("idOnAccount3")) result.add(photo3);
                return result;
            }
        });
    }

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
                .content("{\"accountType\": \"FB\", \"idOnAccount\": \"idOnAccount3\", \"sourceUrl\": \"fakeurl\", \"position\": \"5\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();
        PhotoResource photoResource = new ObjectMapper().readValue(result.getResponse().getContentAsByteArray(), PhotoResource.class);
        assertEquals("url3", photoResource.getSourceUrl());
        assertEquals((Integer)5, photoResource.getPosition());
        assertEquals((Long)2L, jdbcTemplate.queryForObject("select count(*) from user_photos where user_id=1", Long.class));

    }

    @Test
    public void testSetUserPhotos() throws Exception {
        List<PhotoResource> photoResourceList = Arrays.asList(createPhotoResource("FB", "idOnAccount1", 0, "fakeurl1"),
                createPhotoResource("FB", "idOnAccount2", 1, "fakeurl2"));

        mockMvc.perform(post("/api/user/1/photos")
                    .with(bearerToken(1L))
                    .contentType(MediaType.APPLICATION_JSON)
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

    private PhotoResource createPhotoResource(String accountType, String idOnAccount, int position, String url) {
        PhotoResource photoResource = new PhotoResource();
        photoResource.setAccountType(accountType);
        photoResource.setIdOnAccount(idOnAccount);
        photoResource.setPosition(position);
        photoResource.setSourceUrl(url);
        return photoResource;
    }
}