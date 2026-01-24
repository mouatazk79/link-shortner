package com.klaa.springboot4demo.security.code;

import com.klaa.springboot4demo.security.token.TokenPair;
import com.klaa.springboot4demo.security.token.TokenPairService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthCodeExchangeController {
    TokenPairService tokenPairService;
    @GetMapping("/code/exchange")
    public TokenPair getCode(@RequestParam String code) {
        val tokenPair=tokenPairService.get(code);
        if (tokenPair.isEmpty()){
            throw new RuntimeException();
        }
        tokenPairService.remove(code);
        return tokenPair.get();
    }
}
