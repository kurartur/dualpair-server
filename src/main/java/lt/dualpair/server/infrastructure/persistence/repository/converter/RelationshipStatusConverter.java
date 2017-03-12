package lt.dualpair.server.infrastructure.persistence.repository.converter;

import lt.dualpair.server.domain.model.user.RelationshipStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RelationshipStatusConverter implements AttributeConverter<RelationshipStatus, String> {

    @Override
    public String convertToDatabaseColumn(RelationshipStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public RelationshipStatus convertToEntityAttribute(String dbData) {
        return RelationshipStatus.fromCode(dbData);
    }
}

