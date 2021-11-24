package com.cema.users.config.filters;

import com.cema.users.constants.Constants;
import com.cema.users.services.jwt.TokenService;
import com.cema.users.domain.CemaUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserDetailsService userDetailsServiceImpl;

    private final TokenService tokenServiceImpl;

    public JwtRequestFilter(UserDetailsService userDetailsServiceImpl, TokenService tokenServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.isBlank(requestTokenHeader)) {
            String username = null;
            String jwtToken = null;
            if (requestTokenHeader.startsWith(Constants.BEARER_PREFIX)) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = tokenServiceImpl.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Unable to get JWT Token", e);
                } catch (ExpiredJwtException e) {
                    LOG.warn("JWT Token has expired", e);
                }
            } else {
                LOG.warn("Incorrect JWT Token: {}", requestTokenHeader);
            }

            // Once we get the token validate it.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                CemaUserDetails userDetails = (CemaUserDetails) userDetailsServiceImpl.loadUserByUsername(username);

                if (tokenServiceImpl.validateToken(jwtToken, userDetails)) {
                    userDetails.setAuthToken(requestTokenHeader);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }

}