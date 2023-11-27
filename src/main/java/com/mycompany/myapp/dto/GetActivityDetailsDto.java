package com.mycompany.myapp.dto;

import com.mycompany.myapp.domain.Tag;
import com.mycompany.myapp.domain.User;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetActivityDetailsDto extends GetActivityDto {

    @NotNull
    private List<String> tags;

    @NotNull
    private List<String> participants;

    @NotNull
    private Boolean isParticipating;
}
