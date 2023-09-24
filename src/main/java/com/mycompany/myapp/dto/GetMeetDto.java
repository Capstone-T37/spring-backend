package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMeetDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;

    @NotNull
    private String description;
}
