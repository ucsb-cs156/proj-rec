package edu.ucsb.cs156.rec.testconfig;

import edu.ucsb.cs156.rec.config.SecurityConfig;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


import edu.ucsb.cs156.rec.services.CurrentUserService;
import edu.ucsb.cs156.rec.services.GrantedAuthoritiesService;
import org.springframework.context.annotation.Import;

@TestConfiguration
@AutoConfigureDataJpa
@Import(SecurityConfig.class)
public class TestConfig {

    @Bean
    public CurrentUserService currentUserService() {
        return new MockCurrentUserServiceImpl();
    }

    @Bean
    public GrantedAuthoritiesService grantedAuthoritiesService() {
        return new GrantedAuthoritiesService();
    }

}
