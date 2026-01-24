package com.klaa.springboot4demo.security.ott;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OneTimeTokenSender implements OneTimeTokenGenerationSuccessHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException, ServletException {
        //todo this is used to send and email to the user
        System.out.println(oneTimeToken.getTokenValue()+"   "+oneTimeToken.getUsername());
    }
}
