package com.mycompany.myapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetMeetDto extends MeetBaseDto {

    @NotNull
    private Long id;

    @NotNull
    private String userName;

    @NotNull
    private Boolean isEnabled;

    @NotNull
    private Boolean isRequestSent;
}
