package com.pilot.common.config.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pilot.common.constant.AuthConstants;
import com.pilot.common.util.TokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		return path.equals("/api/login");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, RuntimeException {
		String jwt = resolveToken(request);
		
		if(SecurityContextHolder.getContext().getAuthentication() == null || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			SecurityContextHolder.clearContext();
		}
		//Check token
		if(StringUtils.isBlank(jwt) || !TokenUtils.isValidToken(jwt)) {
			SecurityContextHolder.clearContext();
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(AuthConstants.AUTH_HEADER);
		if(StringUtils.isNotBlank(token) && token.startsWith(AuthConstants.TOKEN_PREFIX)) {
			return token.substring(7);
		}
		return null;
	}
}
