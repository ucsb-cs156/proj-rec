package edu.ucsb.cs156.rec.interceptors;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Default;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.management.relation.Role;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@SpringBootTest
@AutoConfigureMockMvc
public class RoleInterceptorTests extends ControllerTestCase{
    @MockBean
    UserRepository userRepository;

    @Autowired
    private HandlerMappingIntrospector mapping;

    @BeforeEach
    public void testLogin(){
        HashMap<String, Object> values = new HashMap<>();
        values.put("email", "joegaucho@ucsb.edu");
        values.put("googleSub", "googleSub");
        values.put("pictureUrl", "pictureUrl");
        values.put("fullName", "Joe Gaucho");
        values.put("givenName", "Joe");
        values.put("familyName", "Gaucho");
        values.put("emailVerified", true);
        values.put("locale", "en");
        values.put("hostedDomain", "ucsb.edu");

        Set<GrantedAuthority> credentials = new HashSet<>();
        credentials.add(new SimpleGrantedAuthority("ROLE_USER"));
        credentials.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        credentials.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        credentials.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

        OAuth2User user = new DefaultOAuth2User(credentials,values,"email");
        Authentication auth = new OAuth2AuthenticationToken(user, credentials, "google");
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(auth); 
    }

    @Test
    public void RoleInterceptorExists() throws Exception{
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/currentuser");
        HandlerExecutionChain chain = mapping.getMatchableHandlerMapping(req).getHandler(req);

        assert chain != null;
        Optional<HandlerInterceptor> RoleInterceptor = chain.getInterceptorList().stream().filter(RoleInterceptor.class::isInstance).findFirst();

        assertTrue(RoleInterceptor.isPresent());
    }
            
}

// private long id;
//   private String email;
//   private String googleSub;
//   private String pictureUrl;
//   private String fullName;
//   private String givenName;
//   private String familyName;
//   private boolean emailVerified;
//   private String locale;
//   private String hostedDomain;
//   @Builder.Default
//   private boolean admin=false;
//   @Builder.Default
//   private boolean student=false;
//   @Builder.Default
//   private boolean professor=false;