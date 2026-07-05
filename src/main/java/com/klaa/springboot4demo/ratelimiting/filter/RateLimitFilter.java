package com.klaa.springboot4demo.ratelimiting.filter;

import com.klaa.springboot4demo.ratelimiting.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitingService rateLimitingService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIP=getClientIP(request);
        Bucket tokenBucket=rateLimitingService.resolveBucket(clientIP);
        var prob=tokenBucket.tryConsumeAndReturnRemaining(1);
        if (prob.isConsumed()){
            response.addHeader("X-Rate-Limit-Remaining",String.valueOf(prob.getRemainingTokens()));
            filterChain.doFilter(request,response);
        }else {
            var waitForRefill=prob.getNanosToWaitForRefill()/1_000_000_000L;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds",String.valueOf(waitForRefill));
            response.setContentType("application/json");
            String jsonResponse= """
                    {"error": "Too Many Requests"}
                    """;
            response.getWriter().write(jsonResponse);
        }

    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null||xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        } else {
            return xfHeader.split(",")[0].trim();

        }
    }
}
