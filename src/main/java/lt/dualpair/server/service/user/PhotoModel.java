package lt.dualpair.server.service.user;

public class PhotoModel {

    private Long id;
    private byte[] photo;
    private int position;

    public PhotoModel(Long id, byte[] photo, int position) {
        this.id = id;
        this.photo = photo;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public int getPosition() {
        return position;
    }
}
