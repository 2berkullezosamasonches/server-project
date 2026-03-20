package com.warehouse.warehouse_manager.security;

import com.warehouse.warehouse_manager.model.SessionStatus;
import com.warehouse.warehouse_manager.model.UserSession;
import com.warehouse.warehouse_manager.repository.UserSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Важно: используем стандартный User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserSessionRepository userSessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        // 1. Проверяем валидность токена
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 2. Проверяем сессию в БД
            Optional<UserSession> session = userSessionRepository.findByAccessToken(token);

            if (session.isEmpty() || session.get().getStatus() != SessionStatus.ACTIVE) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been revoked or session is inactive");
                return;
            }

            // 3. Извлекаем данные из токена
            String username = jwtTokenProvider.getUsername(token);
            Claims claims = jwtTokenProvider.getClaims(token);
            List<String> roles = extractRoles(claims);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase().replace("ROLE_", "")))
                    .collect(Collectors.toList());

            // --- ИСПРАВЛЕНИЕ ТУТ ---
            // Вместо строки 'username' создаем объект UserDetails.
            // Это гарантирует, что .getPrincipal() вернет не null и не String, а объект.
            UserDetails userDetails = new User(username, "", authorities);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
            );

            // Добавляем детали запроса (IP и т.д.)
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Устанавливаем аутентификацию в контекст
            SecurityContextHolder.getContext().setAuthentication(auth);
            // ------------------------
        }

        filterChain.doFilter(request, response);
    }

    private List<String> extractRoles(Claims claims) {
        Object rolesObj = claims.get("roles");
        List<String> roles = new ArrayList<>();
        if (rolesObj instanceof List<?>) {
            for (Object o : (List<?>) rolesObj) {
                roles.add(String.valueOf(o));
            }
        }
        return roles;
    }
}