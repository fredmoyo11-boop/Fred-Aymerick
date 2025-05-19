package com.sep.backend.auth;

import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;

    public UserDetailsServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserDetailsService expects username, we use email instead
        String email = username.toLowerCase();
        String role = accountService.getRoleByEmail(email);

        var password = (switch (role) {
            case Roles.CUSTOMER -> accountService.getCustomerByEmail(email);
            case Roles.DRIVER -> accountService.getDriverByEmail(email);
            default -> throw new UsernameNotFoundException(email);
        }).getPassword();

        return User.builder()
                .username(email.toLowerCase())
                .password(password)
                .roles(role)
                .build();
    }
}
