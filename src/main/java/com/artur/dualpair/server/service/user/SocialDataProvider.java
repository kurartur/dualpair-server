package com.artur.dualpair.server.service.user;

import com.artur.dualpair.server.domain.model.user.User;

public interface SocialDataProvider {

    String getAccountId();

    User enhanceUser(User user) throws SocialDataException;

}
