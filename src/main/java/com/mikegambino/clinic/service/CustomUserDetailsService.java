package com.mikegambino.clinic.service;

import com.mikegambino.clinic.model.User;
import com.mikegambino.clinic.repository.UserRepository;
import com.mikegambino.clinic.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase());
        if (user == null) throw new UsernameNotFoundException("User " + username + " not found");
        return UserPrincipal.create(user);
    }
}
