package com.artur.dualpair.server.domain.model.socionics;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sociotypes")
public class Sociotype implements Serializable {

    public enum Code1 { LII,  ILE,  ESE,  SEI,  LSI,  SLE,  EIE,  IEI,  ESI,  SEE,  LIE,  ILI,  EII,  IEE,  LSE,  SLI }
    public enum Code2 { INTJ, ENTP, ESFJ, ISFP, ISTJ, ESTP, ENFJ, INFP, ISFJ, ESFP, ENTJ, INTP, INFJ, ENFP, ESTJ, ISTP }

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Code1 code1;

    @Enumerated(EnumType.STRING)
    private Code2 code2;

    private Sociotype() {}

    protected Sociotype(Integer id, Code1 code1, Code2 code2) {
        this.id = id;
        this.code1 = code1;
        this.code2 = code2;
    }

    public Integer getId() {
        return id;
    }

    public Code1 getCode1() {
        return code1;
    }

    public Code2 getCode2() {
        return code2;
    }

    public static class Builder {

        protected Integer id;

        protected Code1 code1;

        protected Code2 code2;

        public Builder() {}

        public Builder id(Integer id) { this.id = id; return this; }

        public Builder code1(Code1 code) { this.code1 = code; return this; }

        public Builder code2(Code2 code) { this.code2 = code; return this; }

        public Sociotype build() { return new Sociotype(id, code1, code2); }

    }

}
