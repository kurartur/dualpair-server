package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.user.Device;
import lt.dualpair.server.domain.model.user.DeviceRepository;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.domain.model.user.UserTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DeviceControllerTest {

    private DeviceController deviceController = new DeviceController();
    private DeviceRepository deviceRepository = mock(DeviceRepository.class);
    private User principal = UserTestUtils.createUser(1L);

    @Before
    public void setUp() throws Exception {
        deviceController.setDeviceRepository(deviceRepository);
    }

    @Test
    public void testRegisterDevice() throws Exception {
        doReturn(Optional.empty()).when(deviceRepository).findOne("123");
        ResponseEntity responseEntity = deviceController.registerDevice("123", principal);
        ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
        verify(deviceRepository, times(1)).save(deviceCaptor.capture());
        assertEquals("123", deviceCaptor.getValue().getId());
        assertEquals((Long)1L, deviceCaptor.getValue().getUser().getId());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testRegisterDevice_alreadyExists() throws Exception {
        doReturn(Optional.of(new Device("123", new User()))).when(deviceRepository).findOne("123");
        ResponseEntity responseEntity = deviceController.registerDevice("123", principal);
        verify(deviceRepository, never()).save(any(Device.class));
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

}