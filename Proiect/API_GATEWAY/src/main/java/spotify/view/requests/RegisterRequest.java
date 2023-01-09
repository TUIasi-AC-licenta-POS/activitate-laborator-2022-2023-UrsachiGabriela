package spotify.view.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import spotify.utils.enums.UserRoles;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    private String name;
    private String password;
    private String role;
}
