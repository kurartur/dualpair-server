package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import lt.dualpair.server.service.user.SocialUserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SociotypesControllerTest {

    private SociotypesController sociotypesController = new SociotypesController();
    private SocialUserService socialUserService = mock(SocialUserService.class);
    private User principal = UserTestUtils.createUser(1L);

    @Before
    public void setUp() throws Exception {
        sociotypesController.setSocialUserService(socialUserService);
    }

    @Test
    public void testSetSociotypes() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        ResponseEntity response = sociotypesController.setSociotypes(1L, codes, principal);
        verify(socialUserService, times(1)).setUserSociotypes(1L, sociotypeCodes);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/user/1", response.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetSociotypes_invalidUser() throws Exception {
        String[] codes = {"EII"};
        ResponseEntity response = sociotypesController.setSociotypes(2L, codes, principal);
        verify(socialUserService, never()).setUserSociotypes(any(Long.class), any(Set.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetSociotypes_error() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSociotypes(1L, sociotypeCodes);
        try {
            sociotypesController.setSociotypes(1L, codes, principal);
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

}