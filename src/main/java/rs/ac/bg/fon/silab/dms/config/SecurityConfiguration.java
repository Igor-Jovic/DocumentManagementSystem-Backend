package rs.ac.bg.fon.silab.dms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.ac.bg.fon.silab.dms.core.model.Role;
import rs.ac.bg.fon.silab.dms.security.CustomAuthenticationProvider;
import rs.ac.bg.fon.silab.dms.security.SecurityFilter;

/**
 * @author Sinisa Komarica
 */
@Configuration
public class SecurityConfiguration {

    //this should be used by filter after it is implemented
// TODO:revisit after filter implementation
//    @Bean
//    public TokenService tokenService() {
//        return new InMemoryTokenService();
//    }

    //    @Configuration
//    protected static class GlobalSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
//        @Override
//        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.authenticationProvider(localAuthenticationProvider);
//        }
//    }
    @EnableWebSecurity
    @Configuration
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.addFilterBefore(new SecurityFilter(), UsernamePasswordAuthenticationFilter.class);

            http.authorizeRequests()
                    .antMatchers("/api-status").permitAll()
                    .antMatchers("/admin").hasRole(Role.ADMIN.toString())
                    .antMatchers("/**").authenticated();

            http.exceptionHandling()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .csrf().disable();
        }
    }
}