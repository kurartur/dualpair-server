package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.core.photo.Photo;
import lt.dualpair.core.socionics.Sociotype;
import lt.dualpair.core.user.*;
import lt.dualpair.server.interfaces.resource.BaseResourceAssemblerTest;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResource;
import lt.dualpair.server.interfaces.resource.socionics.SociotypeResourceAssembler;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResourceAssemblerTest extends BaseResourceAssemblerTest {

    private UserResourceAssembler userResourceAssembler = new UserResourceAssembler();
    private SociotypeResourceAssembler sociotypeResourceAssembler = mock(SociotypeResourceAssembler.class);
    private PhotoResourceAssembler photoResourceAssembler = mock(PhotoResourceAssembler.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        userResourceAssembler.setSociotypeResourceAssembler(sociotypeResourceAssembler);
        userResourceAssembler.setPhotoResourceAssembler(photoResourceAssembler);
    }

    @Test
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        user.setDescription("description");
        user.setRelationshipStatus(RelationshipStatus.IN_RELATIONSHIP);
        user.setGender(Gender.FEMALE);

        Set<Sociotype> sociotypes = new HashSet<>(Arrays.asList(new Sociotype.Builder().build()));
        user.setSociotypes(sociotypes);
        Set<SociotypeResource> sociotypeResources = new HashSet<>();
        doReturn(new ArrayList<>(sociotypeResources)).when(sociotypeResourceAssembler).toResources(sociotypes);

        UserLocation location = new UserLocation(user, 10.0, 11.0, "LT", "Vilnius");
        user.addLocation(location, 1);

        Photo photo = new Photo();
        user.setPhotos(Arrays.asList(photo));

        List<PhotoResource> photoResources = new ArrayList<>();
        when(photoResourceAssembler.toResources(user.getPhotos())).thenReturn(photoResources);

        UserAccount userAccount = new UserAccount(user);
        userAccount.setAccountType(UserAccount.Type.FACEBOOK);
        user.setUserAccounts(new HashSet<>(Arrays.asList(userAccount)));

        UserResource userResource = userResourceAssembler.toResource(new UserResourceAssembler.AssemblingContext(user, true, true));

        assertEquals("name", userResource.getName());
        assertEquals((Integer)0, userResource.getAge());
        assertEquals(birthday, userResource.getDateOfBirth());
        assertEquals("description", userResource.getDescription());
        assertEquals(sociotypeResources, userResource.getSociotypes());
        assertEquals(1, userResource.getLocations().size());
        assertEquals(photoResources, userResource.getPhotos());
        assertEquals(1, userResource.getAccounts().size());
        assertEquals("IR", userResource.getRelationshipStatus());
        assertEquals("F", userResource.getGender());
    }

}