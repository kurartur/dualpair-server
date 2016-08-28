package lt.dualpair.server.interfaces.resource.user;

import lt.dualpair.server.domain.model.photo.Photo;
import lt.dualpair.server.domain.model.socionics.Sociotype;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserLocation;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserResourceAssemblerTest {

    private UserResourceAssembler userResourceAssembler = new UserResourceAssembler();

    @Test
    @Ignore // TODO move to integration test?
    public void testToResource() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        Date birthday = new Date();
        user.setDateOfBirth(birthday);
        Set<Sociotype> sociotypes = new HashSet<>(Arrays.asList(new Sociotype.Builder().build()));
        user.setSociotypes(sociotypes);
        user.setDescription("description");
        UserLocation location = new UserLocation(user, 10.0, 11.0, "LT", "Vilnius");
        user.addLocation(location);
        Photo photo = new Photo();
        user.setPhotos(Arrays.asList(photo));
        UserResource userResource = userResourceAssembler.toResource(user);
        assertEquals("name", userResource.getName());
        assertEquals((Integer)0, userResource.getAge());
        assertEquals(birthday, userResource.getDateOfBirth());
        assertEquals("description", userResource.getDescription());
        assertTrue(userResource.getLink("search-parameters").getHref().endsWith("user/1/search-parameters"));
    }

}