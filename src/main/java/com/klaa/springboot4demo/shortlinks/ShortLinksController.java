package com.klaa.springboot4demo.shortlinks;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/shortlinks")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ShortLinksController {
    ShortLinksService shortLinksService;

    @GetMapping("/{shortUrlId}")
    public RedirectView redirectToLongUrl(@PathVariable String shortUrlId) {
        val longUrl=shortLinksService.getShortLinkByShortUrlHash(shortUrlId);
        val redirectView= new RedirectView(longUrl);
        redirectView.setPropagateQueryParams(true);
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
