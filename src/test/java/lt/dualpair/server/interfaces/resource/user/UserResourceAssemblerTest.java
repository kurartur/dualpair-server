package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserAccount;
import lt.dualpair.server.domain.model.user.UserLocation;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UserResourceAssemblerTest extends BaseResourceAssemblerTest {

    private UserResourceAssembler userResourceAssembler = new UserResourceAssembler();
    private SociotypeResourceAssembler sociotypeResourceAssembler = mock(SociotypeResourceAssembler.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        userResourceAssembler.setSociotypeResourceAssembler(sociotypeResourceAssembler);
    }

    @Test
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        user.setDescription("description");

        Set<Sociotype> sociotypes = new HashSet<>(Arrays.asList(new Sociotype.Builder().build()));
        user.setSociotypes(sociotypes);
        Set<SociotypeResource> sociotypeResources = new HashSet<>();
        doReturn(new ArrayList<>(sociotypeResources)).when(sociotypeResourceAssembler).toResources(sociotypes);

        UserLocation location = new UserLocation(user, 10.0, 11.0, "LT", "Vilnius");
        user.addLocation(location);

        Photo photo = new Photo();
        user.setPhotos(Arrays.asList(photo));

        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.FACEBOOK);
        user.setUserAccounts(new HashSet<>(Arrays.asList(userAccount)));

        UserResource userResource = userResourceAssembler.toResource(user);

        assertEquals("name", userResource.getName());
        assertEquals((Integer)0, userResource.getAge());
        assertEquals(birthday, userResource.getDateOfBirth());
        assertEquals("description", userResource.getDescription());
        assertTrue(userResource.getLink("search-parameters").getHref().endsWith("user/1/search-parameters"));
        assertEquals(sociotypeResources, userResource.getSociotypes());
        assertEquals(1, userResource.getLocations().size());
        assertEquals(1, userResource.getPhotos().size());
        assertEquals(1, userResource.getAccounts().size());
    }

}