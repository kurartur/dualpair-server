package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResource;
import lt.dualpair.server.interfaces.resource.user.SearchParametersResourceAssembler;
import lt.dualpair.server.service.user.SocialUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class SearchParametersControllerTest {

    private SearchParametersController searchParametersController = new SearchParametersController();
    private SocialUserService socialUserService = mock(SocialUserService.class);
    private SearchParametersResourceAssembler searchParametersResourceAssembler = mock(SearchParametersResourceAssembler.class);

    @Before
    public void setUp() throws Exception {
        searchParametersController.setSocialUserService(socialUserService);
        searchParametersController.setSearchParametersResourceAssembler(searchParametersResourceAssembler);
        User user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testSetSearchParameters_exception() throws Exception {
        SearchParametersResource searchParametersResource = new SearchParametersResource();
        searchParametersResource.setSearchMale(false);
        searchParametersResource.setSearchFemale(false);
        doThrow(new RuntimeException("Error")).when(socialUserService).setUserSearchParameters(eq(1L), any(SearchParameters.class));
        try {
            searchParametersController.setSearchParameters(1L, searchParametersResource);
            fail();
        } catch (Exception re) {
            assertEquals("Error", re.getMessage());
        }
    }

    @Test
    public void testSetSearchParamters_invalidUser() throws Exception {
        ResponseEntity responseEntity = searchParametersController.setSearchParameters(2L, null);
        verify(socialUserService, never()).setUserSearchParameters(any(Long.class), any(SearchParameters.class));
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void testSetSearchParameters() throws Exception {
        SearchParametersResource searchParametersResource = new SearchParametersResource();
        searchParametersResource.setMinAge(20);
        searchParametersResource.setMaxAge(25);
        searchParametersResource.setSearchFemale(true);
        searchParametersResource.setSearchMale(true);
        searchParametersController.setSearchParameters(1L, searchParametersResource);
        ArgumentCaptor<SearchParameters> captor = ArgumentCaptor.forClass(SearchParameters.class);
        verify(socialUserService, times(1)).setUserSearchParameters(eq(1L), captor.capture());
        SearchParameters searchParameters = captor.getValue();
        assertEquals((Integer)20, searchParameters.getMinAge());
        assertEquals((Integer)25, searchParameters.getMaxAge());
        assertTrue(searchParameters.getSearchFemale());
        assertTrue(searchParameters.getSearchMale());
    }

    @Test
    public void testGetSearchParameters() throws Exception {
        User user = new User();
        SearchParameters searchParameters = new SearchParameters();
        user.setSearchParameters(searchParameters);
        when(socialUserService.loadUserById(1L)).thenReturn(user);
        SearchParametersResource resource = new SearchParametersResource();
        when(searchParametersResourceAssembler.toResource(searchParameters)).thenReturn(resource);
        ResponseEntity<SearchParametersResource> response = searchParametersController.getSearchParameters(1L);
        assertEquals(resource, response.getBody());
    }

    @Test
    public void testGetSearchParameters_invalidUser() throws Exception {
        ResponseEntity<SearchParametersResource> response = searchParametersController.getSearchParameters(2L);
        verify(socialUserService, never()).loadUserById(any(Long.class));
        verify(searchParametersResourceAssembler, never()).toResource(any(SearchParameters.class));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}