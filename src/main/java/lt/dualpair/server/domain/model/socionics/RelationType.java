package lt.dualpair.server.domain.model.socionics;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "socionic_relation_types")
public class RelationType implements Serializable {

    public enum Code { DUAL }

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Code code;

    private RelationType() {}

    protected RelationType(Integer id, Code code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public Code getCode() {
        return code;
    }

    public static class Builder {

        protected Integer id;

        protected Code code;

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }

        public Builder code(Code code) { this.code = code; return this; }

        public RelationType build() { return new RelationType(id, code); }

    }

}
