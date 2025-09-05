package com.himanshu.Authifyy.filter;

import com.himanshu.Authifyy.service.AppUserDetailsService;
import com.himanshu.Authifyy.util.Jwtutil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter{
    private final AppUserDetailsService appUserDetailsService;
    private final Jwtutil jwtutil;

    private static final List<String> PUBLIC_URL=List.of("/login","/register","/send-reset-otp","/reset-password","/logout");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path=request.getServletPath();
        if(PUBLIC_URL.contains(path)){
            filterChain.doFilter(request,response);
            return;
        }
        String jwt=null;
        String email=null;
        final String authorizationHeader=request.getHeader("Authorization");
        if(authorizationHeader!=null&& authorizationHeader.startsWith("Bearer")){
            jwt=authorizationHeader.substring(7);
        }
        //2 if not found header
        if(jwt==null){
            Cookie[] cookies= request.getCookies();
            if(cookies!=null){
                for (Cookie cookie:cookies){
                    if("jwt".equals(cookie.getName())){
                        jwt=cookie.getValue();
                        break;
                    }
                }


            }
        }
        //3 validate token and set the security context
        if(jwt!=null) {
            email=jwtutil.extractEmail(jwt);
            if(email!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=appUserDetailsService.loadUserByUsername(email);
                if(jwtutil.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken=
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request,response);
    }

}
