package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.match.Response;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MatchResponseConverter implements AttributeConverter<Response, String> {

    @Override
    public String convertToDatabaseColumn(Response attribute) {
        return attribute.getCode();
    }

    @Override
    public Response convertToEntityAttribute(String dbData) {
        return Response.fromCode(dbData);
    }
}
