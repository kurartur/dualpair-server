package lt.dualpair.server.interfaces.resource;

import org.junit.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseResourceAssemblerTest {

    @Before
    public void setUp() throws Exception {
        String localHost = "http://localhost";
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getRequestURL()).thenReturn(new StringBuffer(localHost));
        when(httpServletRequestMock.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(httpServletRequestMock.getRequestURI()).thenReturn(localHost);
        when(httpServletRequestMock.getContextPath()).thenReturn("");
        when(httpServletRequestMock.getServletPath()).thenReturn("");
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

}
