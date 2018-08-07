package lt.dualpair.server.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Component
public class PhotoFileHelper {

    private String photoLocation;

    @Inject
    public PhotoFileHelper(@Value("${photoDir}") String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String save(byte[] photo, long userId, int position) {
        try {
            new File(photoLocation + File.separator + userId).mkdir();
            String filename = UUID.randomUUID().toString() + ".jpg";
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(photo));
            File output = new File(photoLocation + File.separator + userId + File.separator + filename);
            ImageIO.write(img, "jpg", output);
            return filename;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public byte[] read(long userId, String filename) {
        try {
            return Files.readAllBytes(new File(photoLocation + File.separator + userId + File.separator + filename).toPath());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
