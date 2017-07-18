package lt.dualpair.server.infrastructure.notification.gcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import lt.dualpair.core.user.Device;
import lt.dualpair.core.user.DeviceRepository;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationSenderImpl implements NotificationSender {

    private static final Logger logger = Logger.getLogger(NotificationSenderImpl.class);

    private static final int RETRY_COUNT = 3;

    private DeviceRepository deviceRepository;

    private Sender messageSender;

    @Override
    public void sendNotification(Notification notification) {
        List<String> ids = getUserDeviceIds(notification.getUserId());
        if (!ids.isEmpty()) {
            try {
                messageSender.send(createMessage(notification), ids, RETRY_COUNT);
            } catch (IOException ioe) {
                logger.error("Unable to send notification " + notification + " to devices " + Arrays.toString(ids.toArray()), ioe);
            }
        }
    }

    private List<String> getUserDeviceIds(Long userId) {
        return deviceRepository.findUserDevices(userId).stream().map(Device::getId).collect(Collectors.toList());
    }

    private Message createMessage(Notification notification) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new Message.Builder()
                .addData("to", notification.getUserId().toString())
                .addData("type", notification.getNotificationType().name())
                .addData("payload", mapper.writeValueAsString(notification.getPayload()))
                .build();
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setMessageSender(Sender messageSender) {
        this.messageSender = messageSender;
    }
}
