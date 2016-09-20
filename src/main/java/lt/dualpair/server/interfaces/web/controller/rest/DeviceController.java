package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.domain.model.user.Device;
import lt.dualpair.server.domain.model.user.DeviceRepository;
import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.authentication.ActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DeviceController {

    private DeviceRepository deviceRepository;

    @RequestMapping(method = RequestMethod.POST, path = "/device")
    public ResponseEntity registerDevice(@RequestParam(name="id", required = true) String deviceId, @ActiveUser User principal) {
        Optional<Device> device = deviceRepository.findOne(deviceId);
        if (device.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Device newDevice = new Device(deviceId, principal);
            deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

}
