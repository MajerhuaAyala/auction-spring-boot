package com.auction.auction.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String name;
    private String email;
    private String aboutMe;
    private long id;
}
