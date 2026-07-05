package com.klaa.springboot4demo.shortlinks;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.klaa.springboot4demo.exceptions.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ShortLinksService {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    ShortLinksRepository shortLinksRepository;
    ShortLinkMapper shortLinkMapper;
    ShortLinksProperties shortLinksProperties;
    @Cacheable(value = "shortLinks", key = "#shortLinkId")
    public ShortLink getShortLinkByShortUrlHash(String shortLinkId){
        val existedShortLink=shortLinksRepository.findById(UUID.fromString(shortLinkId));
        if(existedShortLink.isPresent()){
            return existedShortLink.get();
        }
        throw  new CustomException("short link does not exist");
    }
    public void createShortLink(String longUrl){
        Random randomKey = new Random();
        int low = 10;
        int high = 2000;
        int key = randomKey.nextInt(high - low) + low;
        new ShortLink();
        val shortLink = ShortLink.builder()
                .longUrl(longUrl)
                .shortUrlKey(generateRandomShortUrl(String.valueOf(key)))
                .build();
        shortLinksRepository.save(shortLink);
    }

    private String generateRandomShortUrl(String key){
        char[] chars = CHARS.toCharArray();
        char[] randomChars = new char[shortLinksProperties.getShortUrlKeyLength()];
        Random random = new Random();
        for( int i=0;i<shortLinksProperties.getShortUrlKeyLength();i++){
            int index=random.nextInt(chars.length);
            randomChars[i]=chars[index];
        }

        HashCode hash = Hashing.sha256().hashString(new String(randomChars) +key, StandardCharsets.UTF_8);
        return BaseEncoding.base64Url()
                .omitPadding()
                .encode(hash.asBytes())
                .substring(0, 7);
    }

    public Page<ShortLinkDto> getShortLinks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return shortLinksRepository.findAll(pageable).map(shortLinkMapper::shortLinkToShortLinkDto);
    }

    public void updateShortLink(ShortLinkDto shortLinkDto) {
        val existedShortLink=shortLinksRepository.findById(UUID.fromString(shortLinkDto.getId()));
        if(existedShortLink.isPresent()){
           existedShortLink.get().setLongUrl(shortLinkDto.getLongUrl());
           shortLinksRepository.save(existedShortLink.get());
        }
        throw  new CustomException("short link does not exist");
    }

    @CacheEvict(value = "shortLinks", key = "#shortLinkId")
    public void deleteShortLink(String shortLinkId) {
        val existedShortLink=shortLinksRepository.findById(UUID.fromString(shortLinkId));
        if(existedShortLink.isEmpty()){
            throw  new CustomException("short link does not exist");
        }
        existedShortLink.ifPresent(shortLinksRepository::delete);
    }
}
