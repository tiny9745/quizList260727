package com.example.quizList260727.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 提供密碼加密/驗證用的 PasswordEncoder。 目前採用 BCrypt，強度（strength）預設為 10，數字愈高愈安全但也愈慢， 一般專案
 * 10~12 即可，不建議隨意調太高造成回應變慢。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 須注意此處將security全數放行，等同幾乎無安全驗證
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				// 開啟CORS支援，並套用下方 corsConfigurationSource() 的規則，
				// 否則即使 authorizeHttpRequests 全部 permitAll，
				// 瀏覽器的 preflight(OPTIONS) 請求還是會被擋下
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").permitAll().anyRequest().permitAll())
				.build();
	}

	/**
	 * CORS 設定：允許前端開發伺服器(ng serve，預設4200port)呼叫本後端API。
	 * 正式環境部署後，記得把 setAllowedOrigins 換成實際前端網域。
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}