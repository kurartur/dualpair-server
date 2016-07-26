package com.artur.dualpair.server.infrastructure.notification.gcm;

import com.artur.dualpair.server.domain.model.user.Device;
import com.artur.dualpair.server.domain.model.user.DeviceRepository;
import com.artur.dualpair.server.infrastructure.notification.Notification;
import com.artur.dualpair.server.infrastructure.notification.NotificationSender;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
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

    private Message createMessage(Notification notification) {
        return new Message.Builder()
                .addData("message", notification.getMessage())
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
