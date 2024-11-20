package edu.ucsb.cs156.rec.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.stream.Collectors;
import edu.ucsb.cs156.rec.entities.User;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // only process for users logged in using OAuth2  
        if(authentication.getClass() == OAuth2AuthenticationToken.class){
            // extract principle (aka. email, name, etc...)
            OAuth2User principle =  ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = principle.getAttribute("email");

            Optional<User> optionalUser = userRepository.findByEmail(email);
           
            // continue only if user is in db
            if(optionalUser.isPresent()){
                User user = optionalUser.get();

                // Retrieve currently assigned ROLES for this user
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                // Strip user's current roles
                Set<GrantedAuthority> revisedAuthorities = authorities.stream().filter(
                    grantedAuth -> !grantedAuth.getAuthority().equals("ROLE_ADMIN")
                                && !grantedAuth.getAuthority().equals("ROLE_PROFESSOR")
                                && !grantedAuth.getAuthority().equals("ROLE_STUDENT"))
                    .collect(Collectors.toSet());
                
                // Dynamically assign roles based on user's role in the database
                if (user.getAdmin()) {
                    revisedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                if (user.getProfessor()) {
                    revisedAuthorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
                }
                if (user.getStudent()) {
                    revisedAuthorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                }
                
                // Create new authentication object with revised roles
                Authentication newAuthentication = new OAuth2AuthenticationToken( principle, revisedAuthorities, ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());

                // Replace the current oauth2 object with the new one, updated with new roles.
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }
        }
        return true;
    }
}