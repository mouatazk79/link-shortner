package com.klaa.springboot4demo.shortlinks;

import com.klaa.springboot4demo.analytics.ShortLinkRedirectAnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static org.springframework.http.HttpHeaders.REFERER;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestController
@RequestMapping("/shortlinks")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ShortLinksController {
    ShortLinksService shortLinksService;
    ShortLinkRedirectAnalyticsService analyticsService;

    @GetMapping("/{shortUrlId}")
    public RedirectView redirectToLongUrl(@PathVariable String shortUrlId,
                                          @RequestHeader(name = USER_AGENT, required = false) String userAgent,
                                          @RequestHeader(name = REFERER, required = false) String referer,
                                          @RequestHeader HttpHeaders headers,
                                          HttpServletRequest request
    ) {
        val shortLink=shortLinksService.getShortLinkByShortUrlHash(shortUrlId);
        val redirectView= new RedirectView(shortLink.getLongUrl());
        redirectView.setPropagateQueryParams(true);
        String remoteAddr = request.getRemoteAddr();
        String requestURL = request.getRequestURL().toString();
        analyticsService.recordRedirect(shortLink.getId(), userAgent, referer, headers, remoteAddr, requestURL);
        return redirectView;
    }
    @PostMapping
    public ResponseEntity<?> creatShortUrl(@RequestParam String longURL){
        shortLinksService.createShortLink(longURL);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<Page<ShortLinkDto>> getShortLinks( @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(shortLinksService.getShortLinks(page,size));
    }
    @PatchMapping
    public void updateShortLink(@RequestBody ShortLinkDto shortLinkDto){
        shortLinksService.updateShortLink(shortLinkDto);
    }
    @DeleteMapping("/{shortLinkId}")
    public void deleteShortLink(@PathVariable String shortLinkId){
        shortLinksService.deleteShortLink(shortLinkId);
    }
}
