package lt.dualpair.server.domain.model.socionics.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_choices")
public class Choice {

    @Id
    private Integer id;

    private String code;

    @Column(name = "remote_value")
    private String remoteValue;

    protected Choice(Integer id, String code, String remoteValue) {
        this.id = id;
        this.code = code;
        this.remoteValue = remoteValue;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getRemoteValue() {
        return remoteValue;
    }

    public static class Builder {

        protected Integer id;

        protected String code;

        protected String remoteValue;

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }

        public Builder code(String code) { this.code = code; return this; }

        public Builder remoteValue(String remoteValue) { this.remoteValue = remoteValue; return this; }

        public Choice build() { return new Choice(id, code, remoteValue); }

    }
}
