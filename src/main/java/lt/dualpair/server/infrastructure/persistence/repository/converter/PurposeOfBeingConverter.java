package lt.dualpair.server.infrastructure.persistence.repository.converter;

import lt.dualpair.core.user.PurposeOfBeing;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PurposeOfBeingConverter implements AttributeConverter<PurposeOfBeing, String> {

    @Override
    public String convertToDatabaseColumn(PurposeOfBeing attribute) {
        return attribute.getCode();
    }

    @Override
    public PurposeOfBeing convertToEntityAttribute(String dbData) {
        return PurposeOfBeing.fromCode(dbData);
    }
}

