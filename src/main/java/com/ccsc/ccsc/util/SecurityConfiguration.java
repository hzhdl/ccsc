package com.ccsc.ccsc.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/Chain/**").hasRole("USER")
                .antMatchers("/Chain/**").permitAll()
                .antMatchers("/Contract/**").permitAll()
                .antMatchers("/Subcribe/**").permitAll()
                .antMatchers("/Invocation/**").permitAll()
                .antMatchers("/evaluation/**").permitAll()
                .antMatchers("/Test/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin").password("{noop}adminpass").roles("ADMIN", "USER")
                .and()
                .withUser("spring").password("{noop}123456").roles("USER");

    }

}


