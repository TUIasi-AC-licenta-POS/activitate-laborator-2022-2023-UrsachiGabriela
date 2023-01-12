package spotify.view.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spotify.idmclient.wsdl.StringArray;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    Integer sub;

    StringArray roles;
}
