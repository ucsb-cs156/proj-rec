package edu.ucsb.cs156.rec.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.ControllerTestCase;

@SpringBootTest
@AutoConfigureMockMvc
public class RoleInterceptorTests extends ControllerTestCase{
    
    @MockBean
    UserRepository userRepository;

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @BeforeEach
    public void mockLogin(){
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "cgaucho@ucsb.edu");
        attributes.put("googleSub", "googleSub");
        attributes.put("pictureUrl", "pictureUrl");
        attributes.put("fullName", "fullName");
        attributes.put("givenName", "givenName");
        attributes.put("familyName", "familyName");
        attributes.put("emailVerified", true);
        attributes.put("locale", "locale");
        attributes.put("hostedDomain", "hostedDomain");

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));

        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "email");
        Authentication authentication = new OAuth2AuthenticationToken(user, authorities, "userRegistrationId");
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void RoleInterceptorIsPresent() throws Exception{

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList()
                .stream()
                .filter(RoleInterceptor.class::isInstance)
                .findFirst();

        assertTrue(RoleInterceptor.isPresent());
    }

    @Test
    public void updates_admin_role_when_user_admin_false() throws Exception{
        User user = User.builder()
                        .email("cgaucho@ucsb.edu")
                        .id(15L)
                        .admin(false)
                        .professor(true)
                        .student(true)
                        .build();

        when(userRepository.findByEmail("cgaucho@ucsb.edu")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList()
                .stream()
                .filter(RoleInterceptor.class::isInstance)
                .findFirst();

        assertTrue(RoleInterceptor.isPresent());

        RoleInterceptor.get().preHandle(request, response, chain.getHandler());

        verify(userRepository, times(1)).findByEmail("cgaucho@ucsb.edu");

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean role_admin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean role_professor = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")); 
        boolean role_student = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        assertFalse(role_admin, "ROLE_ADMIN should not be in roles list");
        assertTrue(role_professor, "ROLE_PROFESSOR should be in roles list");
        assertTrue(role_student, "ROLE_STUDENT should be in roles list");
    }

    @Test
    public void updates_professor_role_when_user_professor_false() throws Exception{
        User user = User.builder()
                        .email("cgaucho@ucsb.edu")
                        .id(15L)
                        .admin(true)
                        .professor(false)
                        .student(false)
                        .build();
        
        when(userRepository.findByEmail("cgaucho@ucsb.edu")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList()
                .stream()
                .filter(RoleInterceptor.class::isInstance)
                .findFirst();

        assertTrue(RoleInterceptor.isPresent());

        RoleInterceptor.get().preHandle(request, response, chain.getHandler());
        
        verify(userRepository, times(1)).findByEmail("cgaucho@ucsb.edu");

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean role_admin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean role_professor = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")); 
        boolean role_student = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        assertTrue(role_admin, "ROLE_ADMIN should be in roles list");
        assertFalse(role_professor, "ROLE_PROFESSOR should not be in roles list");
        assertFalse(role_student, "ROLE_STUDENT should not be in roles list");
    }

    @Test
    public void updates_student_role_when_user_student_true() throws Exception{
        User user = User.builder()
                        .email("cgaucho@ucsb.edu")
                        .id(15L)
                        .admin(true)
                        .professor(false)
                        .student(true)
                        .build();
        
        when(userRepository.findByEmail("cgaucho@ucsb.edu")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList()
                .stream()
                .filter(RoleInterceptor.class::isInstance)
                .findFirst();

        assertTrue(RoleInterceptor.isPresent());

        RoleInterceptor.get().preHandle(request, response, chain.getHandler());
        
        verify(userRepository, times(1)).findByEmail("cgaucho@ucsb.edu");

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean role_admin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean role_professor = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")); 
        boolean role_student = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        assertTrue(role_admin, "ROLE_ADMIN should be in roles list");
        assertFalse(role_professor, "ROLE_PROFESSOR should not be in roles list");
        assertTrue(role_student, "ROLE_STUDENT should be in roles list");
    }
    
    @Test
    public void updates_nothing_when_user_not_present() throws Exception {
        User user = User.builder()
                        .email("cgaucho2@ucsb.edu")
                        .id(15L)
                        .admin(false)
                        .professor(false)
                        .student(false)
                        .build();

        when(userRepository.findByEmail("cgaucho2@ucsb.edu")).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList()
                        .stream()
                        .filter(RoleInterceptor.class::isInstance)
                        .findFirst();

        assertTrue(RoleInterceptor.isPresent());

        RoleInterceptor.get().preHandle(request, response, chain.getHandler());

        verify(userRepository, times(1)).findByEmail("cgaucho@ucsb.edu");
        
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean role_admin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean role_professor = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")); 
        boolean role_student = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        assertTrue(role_admin, "ROLE_ADMIN should not be in roles list");
        assertTrue(role_professor, "ROLE_PROFESSOR should not be in roles list");
        assertTrue(role_student, "ROLE_STUDENT should not be in roles list");
    }
}