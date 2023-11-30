package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetTagDto {

    @NotNull
    private Long id;

    @NotNull
    private String title;
}
