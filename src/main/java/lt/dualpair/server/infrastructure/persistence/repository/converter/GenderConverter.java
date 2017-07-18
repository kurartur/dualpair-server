package lt.dualpair.server.infrastructure.persistence.repository.converter;

import lt.dualpair.core.user.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender.getCode();
    }
    @Override
    public Gender convertToEntityAttribute(String code) {
        return Gender.fromCode(code);
    }
}
