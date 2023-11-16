package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRequestDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;
}
