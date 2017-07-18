package lt.dualpair.server.infrastructure.notification.gcm;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import lt.dualpair.core.user.Device;
import lt.dualpair.core.user.DeviceRepository;
import lt.dualpair.core.user.User;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class NotificationSenderImplTest {

    private NotificationSenderImpl notificationSender = new NotificationSenderImpl();
    private Sender messageSender = mock(Sender.class);
    private DeviceRepository deviceRepository = mock(DeviceRepository.class);

    @Before
    public void setUp() throws Exception {
        notificationSender.setMessageSender(messageSender);
        notificationSender.setDeviceRepository(deviceRepository);
    }

    @Test
    public void testSendNotification() throws Exception {
        Notification notification = new Notification<>(1L, NotificationType.NEW_MATCH, "Hello");
        when(deviceRepository.findUserDevices(1L)).thenReturn(new HashSet<>(Arrays.asList(new Device("device1", new User()))));
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        notificationSender.sendNotification(notification);
        verify(messageSender).send(messageArgumentCaptor.capture(), eq(Arrays.asList("device1")), eq(3));
        assertEquals("NEW_MATCH", messageArgumentCaptor.getValue().getData().get("type"));
        assertEquals("\"Hello\"", messageArgumentCaptor.getValue().getData().get("payload"));
        assertEquals("1", messageArgumentCaptor.getValue().getData().get("to"));
    }
}