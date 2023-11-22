package com.mycompany.myapp.dto;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityDto {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private Instant date;
}
