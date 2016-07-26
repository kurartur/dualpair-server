package com.artur.dualpair.server.interfaces.web.controller.rest;

import com.artur.dualpair.server.domain.model.user.Device;
import com.artur.dualpair.server.domain.model.user.DeviceRepository;
import com.artur.dualpair.server.domain.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity registerDevice(@RequestParam(name="id", required = true) String deviceId) {
        Optional<Device> device = deviceRepository.findOne(deviceId);
        if (device.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Device newDevice = new Device(deviceId, getUserPrincipal());
            deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    private User getUserPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
