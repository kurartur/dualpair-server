package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.*;
import lt.dualpair.server.security.UserDetailsImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    private ReportController reportController;
    private UserRepository userRepository;
    private UserReportRepository userReportRepository;

    private User principal = UserTestUtils.createUser();
    private User userBeingReported = UserTestUtils.createUser(2L);

    @Before
    public void setUp() throws Exception {
        userReportRepository = mock(UserReportRepository.class);
        userRepository = mock(UserRepository.class);
        reportController = new ReportController(userReportRepository, userRepository);
        when(userRepository.findById(2L)).thenReturn(Optional.of(userBeingReported));
        when(userReportRepository.getReportCountByUser(eq(principal), any(Date.class))).thenReturn(0);
        when(userReportRepository.findUserReportByUser(userBeingReported, principal)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(principal));
    }

    @Test
    public void report() throws Exception {
        reportController.report(createData(2L), new UserDetailsImpl(1L));
        ArgumentCaptor<UserReport> userReportCaptor = ArgumentCaptor.forClass(UserReport.class);
        verify(userReportRepository, times(1)).save(userReportCaptor.capture());
        UserReport userReport = userReportCaptor.getValue();
        assertEquals((Long)2L, userReport.getUser().getId());
        assertEquals(principal, userReport.getReportedBy());
    }

    @Test
    public void report_whenLimitReached_409response() throws Exception {
        when(userReportRepository.getReportCountByUser(eq(principal), any(Date.class))).thenReturn(5);
        try {
            reportController.report(createData(2L), new UserDetailsImpl(1L));
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("Report limit is reached", ise.getMessage());
        }
        verify(userReportRepository, never()).save(any(UserReport.class));
    }

    @Test
    public void report_whenUserNotFound_400response() throws Exception {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        try {
            reportController.report(createData(3L), new UserDetailsImpl(1L));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("User not found", iae.getMessage());
        }
        verify(userReportRepository, never()).save(any(UserReport.class));
    }

    @Test
    public void report_whenAlreadyReported_409response() throws Exception {
        when(userReportRepository.findUserReportByUser(userBeingReported, principal)).thenReturn(Optional.of(new UserReport(null, null)));
        try {
            reportController.report(createData(2L), new UserDetailsImpl(1L));
            fail();
        } catch (IllegalStateException ise) {
            assertEquals("User already reported", ise.getMessage());
        }
        verify(userReportRepository, never()).save(any(UserReport.class));
    }

    private Map<String, String> createData(Long userId) {
        Map<String, String> data = new HashMap<>();
        data.put("user_id", userId + "");
        return data;
    }
}