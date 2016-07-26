package com.artur.dualpair.server.domain.model.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface DeviceRepository extends CrudRepository<Device, Long> {

    @Query("select d from Device d where d.user.id = ?1")
    Set<Device> findUserDevices(Long userId);

}
