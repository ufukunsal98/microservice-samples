package com.ufuk.accountprovider.Service;

import com.ufuk.accountprovider.Domain.CustomUserDetail;
import com.ufuk.accountprovider.Domain.Users;
import com.ufuk.accountprovider.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityGroupService securityGroupService;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        List<Users> users = userRepository.findByUsername(name);
        if(users.isEmpty()) {
            throw new UsernameNotFoundException("Could not find the user "+name);
        }

        Users user = users.get(0);
        // List<SecurityGroup> securityGroups = securityGroupService.listUserGroups(user.getCompanyId(), user.getId());

//        return new CustomUserDetail(user, securityGroups.stream()
//                .map(e->e.getId())
//                .collect(Collectors.toList()) );
        return new CustomUserDetail(user , null);
    }
}
