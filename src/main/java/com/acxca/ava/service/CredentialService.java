package com.acxca.ava.service;

import com.acxca.ava.repository.UserRepository;
import com.acxca.components.spring.jwt.JwtUserDetail;
import com.acxca.components.spring.service.ICredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialService implements ICredential {
    @Autowired
    private UserRepository userRepository;

    @Override
    public JwtUserDetail selectByUserNameAndPassword(String username, String password) {
        JwtUserDetail u = userRepository.selectByUserNameAndPassword(username,password);

        return u;
    }
}
