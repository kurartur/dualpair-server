package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.core.socionics.SociotypeRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserTestUtils;
import lt.dualpair.server.security.TestUserDetails;
import lt.dualpair.server.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SociotypesControllerTest {

    private SociotypesController sociotypesController = new SociotypesController();
    private UserService socialUserService = mock(UserService.class);
    private SociotypeRepository sociotypeRepository = mock(SociotypeRepository.class);
    private User principal = UserTestUtils.createUser(1L);
    private Set<Sociotype> sociotypes = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        sociotypesController.setUserService(socialUserService);
        sociotypesController.setSociotypeRepository(sociotypeRepository);
        sociotypes.add(new Sociotype.Builder().code1(Sociotype.Code1.EII).build());
        when(sociotypeRepository.findByCode1List(Arrays.asList(Sociotype.Code1.EII))).thenReturn(sociotypes);
        when(socialUserService.loadUserById(1L)).thenReturn(principal);
    }

    @Test
    public void testSetSociotypes() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        ResponseEntity response = sociotypesController.setSociotypes(1L, codes, new TestUserDetails(1L));
        verify(socialUserService, times(1)).setUserSociotypes(principal, sociotypes);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/user/1", response.getHeaders().getLocation().toString());
    }

    @Test
    public void testSetSociotypes_invalidUser() throws Exception {
        String[] codes = {"EII"};
        ResponseEntity response = sociotypesController.setSociotypes(2L, codes, new TestUserDetails(1L));
        verify(socialUserService, never()).setUserSociotypes(any(User.class), any(Set.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testSetSociotypes_error() throws Exception {
        String[] codes = {"EII"};
        Set<Sociotype.Code1> sociotypeCodes = new HashSet<>();
        sociotypeCodes.add(Sociotype.Code1.EII);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSociotypes(principal, sociotypes);
        try {
            sociotypesController.setSociotypes(1L, codes, new TestUserDetails(1L));
            fail();
        } catch (RuntimeException re) {
            assertEquals("Error", re.getMessage());
        }
    }

}