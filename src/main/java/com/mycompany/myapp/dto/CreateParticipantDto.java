package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipantDto {

    @NotNull
    private Long activityId;
}
