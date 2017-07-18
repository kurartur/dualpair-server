package lt.dualpair.server.infrastructure.persistence.repository.converter;

import lt.dualpair.core.user.UserAccount;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<UserAccount.Type, String> {

    @Override
    public String convertToDatabaseColumn(UserAccount.Type attribute) {
        return attribute.getCode();
    }

    @Override
    public UserAccount.Type convertToEntityAttribute(String dbData) {
        return UserAccount.Type.fromCode(dbData);
    }
}
