package com.mycompany.myapp.dto;

import com.mycompany.myapp.domain.Activity;
import java.time.Instant;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetActivityDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private Instant date;

    private String imageUrl;
}
