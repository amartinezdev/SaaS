package com.example.listingai.auth;

import com.example.listingai.user.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class GoogleOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final OidcUserService delegate = new OidcUserService();
    private final UserService users;

    public GoogleOidcUserService(UserService users) {
        this.users = users;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        OidcUser principal = delegate.loadUser(request);
        users.findOrCreate(principal.getSubject(), principal.getEmail(), principal.getFullName());
        return principal;
    }
}
