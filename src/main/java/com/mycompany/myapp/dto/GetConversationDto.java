package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetConversationDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;

    private String imageUrl;
}
