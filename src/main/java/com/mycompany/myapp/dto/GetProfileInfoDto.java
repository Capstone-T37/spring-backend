package com.mycompany.myapp.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileInfoDto {

    private String fullName;
    private String imageUrl;
    private String userName;
}
