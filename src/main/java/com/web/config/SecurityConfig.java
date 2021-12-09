package com.web.config;

import com.web.domain.enums.SocialType;
import com.web.oauth.ClientResources;
import com.web.oauth.UserTokenService;
import com.web.oauth2.CustomOAuth2Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.web.domain.enums.SocialType.FACEBOOK;
import static com.web.domain.enums.SocialType.GOOGLE;
import static com.web.domain.enums.SocialType.KAKAO;

@Configuration
/** @EnableWebSecurity:
 * 웹에서 시큐리티 기능을 사용하겠다는 어노테이션*/
@EnableWebSecurity
//@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
            http
                .authorizeRequests()
                    .antMatchers("/", "/oauth2/**",  "/login/**", "/css/**", "/images/**", "/js/**", "/console/**").permitAll()
                    .antMatchers("/facebook").hasAnyAuthority(FACEBOOK.getRoleType())
                    .antMatchers("/google").hasAnyAuthority(GOOGLE.getRoleType())
                    .antMatchers("/kakao").hasAnyAuthority(KAKAO.getRoleType())
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl("/loginSuccess")
                    .failureUrl("/loginFailure")
                .and()
                    .headers().frameOptions().disable()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                    .formLogin()
                    .successForwardUrl("/board/list")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JESSIONID")
                    .invalidateHttpSession(true)
                .and()
                    .addFilterBefore(filter, CsrfFilter.class)
//                    .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
                    .csrf().disable();

    }

//    @Bean
//    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(filter);
//        registration.setOrder(-100);
//        return registration;
//    }
//
//    private Filter oauth2Filter() {
//        CompositeFilter filter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//        filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK ));
//        filters.add(oauth2Filter(google(), "/login/google", GOOGLE ));
//        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO ));
//        filter.setFilters(filters);
//        return filter;
//
//    }
//
//    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
//        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
//        filter.setRestTemplate(template);
//        filter.setTokenServices(new UserTokenService(client, socialType));
//        filter.setAuthenticationSuccessHandler((request, response, authentication) ->
//                response.sendRedirect("/" + socialType.getValue() + "/complete"));
//        filter.setAuthenticationFailureHandler((request, response, exception) ->
//                response.sendRedirect("/error"));
//        return filter;
//    }
//
//
//    @Bean
//    @ConfigurationProperties("facebook")
//    public ClientResources facebook() {
//        return new ClientResources();
//    }
//
//    @Bean
//    @ConfigurationProperties("google")
//    public ClientResources google() {
//        return new ClientResources();
//    }
//
//    @Bean
//    @ConfigurationProperties("kakao")
//    public ClientResources kakao() {
//        return new ClientResources();
//    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties, @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .jwkSetUri("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .build());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
        if ("google".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }
        if ("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build();
        }
        return null;
    }

}
