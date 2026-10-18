package vn.edu.dlu.khachsan.config;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.dlu.khachsan.service.KhachHangUserDetailsService;
import vn.edu.dlu.khachsan.service.NhanVienUserDetailsService;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain nhanVienFilterChain(HttpSecurity http,
            NhanVienUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // Mỗi phân hệ có AuthenticationManager riêng, không thử xác thực sang bảng còn lại.
        http.securityMatcher("/admin/**")
                .authenticationManager(new ProviderManager(provider))
                .authorizeHttpRequests(quyen -> quyen
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/admin/dang-nhap").permitAll()
                        .requestMatchers("/admin", "/admin/", "/admin/dang-xuat").hasAnyRole("VT01", "VT02", "VT03")
                        .requestMatchers("/admin/tim-phong", "/admin/tim-phong/**").hasAnyRole("VT01", "VT02")
                        .requestMatchers("/admin/dat-phong/can-xac-nhan", "/admin/dat-phong/can-xac-nhan/**",
                                "/admin/nhan-tra-phong/**", "/admin/hoa-don/**").hasRole("VT01")
                        .requestMatchers("/admin/phong/tinh-trang", "/admin/phong/tinh-trang/**").hasRole("VT03")
                        .requestMatchers("/admin/phong", "/admin/phong/**", "/admin/nhan-vien/**",
                                "/admin/bao-cao/**", "/admin/dat-phong/**", "/admin/tham-so/**").hasRole("VT02")
                        .anyRequest().denyAll())
                .formLogin(form -> form.loginPage("/admin/dang-nhap").usernameParameter("email")
                        .loginProcessingUrl("/admin/dang-nhap").defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/dang-nhap?loi").permitAll())
                .logout(logout -> logout.logoutUrl("/admin/dang-xuat")
                        .logoutSuccessUrl("/admin/dang-nhap?dang-xuat").permitAll())
                .exceptionHandling(loi -> loi.accessDeniedPage("/loi/403"));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain khachHangFilterChain(HttpSecurity http,
            KhachHangUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        http.authenticationManager(new ProviderManager(provider))
                .authorizeHttpRequests(quyen -> quyen
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/", "/dang-nhap", "/dang-ky", "/loi/403", "/error",
                                "/css/**", "/js/**", "/vendor/**", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/dat-phong/tim-kiem").permitAll()
                        .requestMatchers("/dat-phong/**", "/thanh-toan/**", "/danh-gia/**", "/tai-khoan/**")
                                .hasRole("KHACH")
                        .anyRequest().permitAll())
                .formLogin(form -> form.loginPage("/dang-nhap").usernameParameter("email")
                        .loginProcessingUrl("/dang-nhap").defaultSuccessUrl("/", true)
                        .failureUrl("/dang-nhap?loi").permitAll())
                .logout(logout -> logout.logoutUrl("/dang-xuat").logoutSuccessUrl("/dang-nhap?dang-xuat").permitAll())
                .exceptionHandling(loi -> loi.accessDeniedPage("/loi/403"));
        return http.build();
    }
}
