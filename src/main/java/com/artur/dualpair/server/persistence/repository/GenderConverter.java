package com.artur.dualpair.server.persistence.repository;

import com.artur.dualpair.server.domain.model.user.User;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<User.Gender, String> {
    @Override
    public String convertToDatabaseColumn(User.Gender gender) {
        return gender.getCode();
    }
    @Override
    public User.Gender convertToEntityAttribute(String code) {
        return User.Gender.fromCode(code);
    }
}
