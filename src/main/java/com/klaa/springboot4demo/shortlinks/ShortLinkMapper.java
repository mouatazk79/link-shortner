package com.klaa.springboot4demo.shortlinks;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ShortLinkMapper {
    ShortLinkMapper INSTANCE = Mappers.getMapper( ShortLinkMapper.class );
    ShortLinkDto shortLinkToShortLinkDto(ShortLink shortLink);
}
