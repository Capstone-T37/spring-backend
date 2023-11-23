package com.mycompany.myapp.dto;

import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetParticipantDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;
}
