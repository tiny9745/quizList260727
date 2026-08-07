package com.example.quizList260727.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 每個請求都會先經過此 Filter：
 * 1. 從 Authorization Header 取出 "Bearer xxx" 的 JWT
 * 2. 驗證合法且未過期後，將登入資訊放進 SecurityContext
 * 3. 沒帶 token 或驗證失敗則不設定登入狀態，
 *    交由 SecurityConfig 的 authorizeHttpRequests 決定該路徑是否需要登入（未登入會回 403）
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	private static final String HEADER_NAME = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader(HEADER_NAME);

		if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(TOKEN_PREFIX.length());

		try {
			final String email = jwtService.extractEmail(jwt);

			if (StringUtils.hasText(email) && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			// token 格式錯誤、過期或簽章不符，都不建立登入狀態
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
}