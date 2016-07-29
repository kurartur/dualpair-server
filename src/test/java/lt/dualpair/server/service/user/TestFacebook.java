package lt.dualpair.server.service.user;

import org.springframework.social.facebook.api.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

public class TestFacebook implements Facebook {

    private UserOperations userOperations;

    @Override
    public AchievementOperations achievementOperations() {
        return null;
    }

    @Override
    public CommentOperations commentOperations() {
        return null;
    }

    @Override
    public EventOperations eventOperations() {
        return null;
    }

    @Override
    public FeedOperations feedOperations() {
        return null;
    }

    @Override
    public FriendOperations friendOperations() {
        return null;
    }

    @Override
    public GroupOperations groupOperations() {
        return null;
    }

    @Override
    public LikeOperations likeOperations() {
        return null;
    }

    @Override
    public MediaOperations mediaOperations() {
        return null;
    }

    @Override
    public OpenGraphOperations openGraphOperations() {
        return null;
    }

    @Override
    public PageOperations pageOperations() {
        return null;
    }

    @Override
    public SocialContextOperations socialContextOperations() {
        return null;
    }

    @Override
    public TestUserOperations testUserOperations() {
        return null;
    }

    @Override
    public UserOperations userOperations() {
        return null;
    }

    public void setUserOperations(UserOperations userOperations) {
        this.userOperations = userOperations;
    }

    @Override
    public RestOperations restOperations() {
        return null;
    }

    @Override
    public String getApplicationNamespace() {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }

    @Override
    public <T> T fetchObject(String objectId, Class<T> type) {
        return null;
    }

    @Override
    public <T> T fetchObject(String objectId, Class<T> type, String... fields) {
        return null;
    }

    @Override
    public <T> T fetchObject(String objectId, Class<T> type, MultiValueMap<String, String> queryParameters) {
        return null;
    }

    @Override
    public <T> PagedList<T> fetchConnections(String objectId, String connectionName, Class<T> type, String... fields) {
        return null;
    }

    @Override
    public <T> PagedList<T> fetchConnections(String objectId, String connectionName, Class<T> type, MultiValueMap<String, String> queryParameters) {
        return null;
    }

    @Override
    public <T> PagedList<T> fetchConnections(String objectId, String connectionName, Class<T> type, MultiValueMap<String, String> queryParameters, String... fields) {
        return null;
    }

    @Override
    public byte[] fetchImage(String objectId, String connectionName, ImageType imageType) {
        return new byte[0];
    }

    @Override
    public byte[] fetchImage(String objectId, String connectionName, Integer width, Integer height) {
        return new byte[0];
    }

    @Override
    public String publish(String objectId, String connectionName, MultiValueMap<String, Object> data) {
        return null;
    }

    @Override
    public void post(String objectId, MultiValueMap<String, Object> data) {

    }

    @Override
    public void post(String objectId, String connectionName, MultiValueMap<String, Object> data) {

    }

    @Override
    public void delete(String objectId) {

    }

    @Override
    public void delete(String objectId, String connectionName) {

    }

    @Override
    public void delete(String objectId, String connectionName, MultiValueMap<String, String> data) {

    }

    @Override
    public String getBaseGraphApiUrl() {
        return null;
    }
}
