package lt.dualpair.server.domain.model.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.Set;

public interface DeviceRepository extends Repository<Device, String> {

    @Query("select d from Device d where d.user.id = ?1")
    Set<Device> findUserDevices(Long userId);

    Optional<Device> findOne(String s);

    Device save(Device device);

}
