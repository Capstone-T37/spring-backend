package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MeetBaseDto {

    @NotNull
    private String description;
}
