package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.core.user.Device;
import lt.dualpair.core.user.DeviceRepository;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DeviceController {

    private DeviceRepository deviceRepository;
    private UserRepository userRepository;

    @Inject
    public DeviceController(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/device")
    public ResponseEntity registerDevice(@RequestParam(name="id", required = true) String deviceId, @ActiveUser UserDetails principal) {
        Optional<Device> device = deviceRepository.findOne(deviceId);
        if (device.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Device newDevice = new Device(deviceId, userRepository.findById(new Long(principal.getUsername())).get());
            deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

}
