package com.example.quizList260727.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.quizList260727.user.entity.User;
import com.example.quizList260727.user.respository.UserRepository;

/**
 * 依 email（作為 username）載入使用者資訊。
 * JwtAuthenticationFilter 在解析出 token 內的 email 後，
 * 會呼叫這裡重新載入使用者，確認帳號仍然存在並取得其權限。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + email));

		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getEmail())
				.password(user.getPassword() == null ? "" : user.getPassword())
				.authorities(new SimpleGrantedAuthority("ROLE_" + user.getPermissions().name()))
				.build();
	}
}