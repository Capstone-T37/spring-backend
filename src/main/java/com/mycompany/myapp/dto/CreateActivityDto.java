package com.mycompany.myapp.dto;

import java.time.Instant;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityDto {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private Instant date;

    @NotNull
    private List<GetTagDto> tags;
}
