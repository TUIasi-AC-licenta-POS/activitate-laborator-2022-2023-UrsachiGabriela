package spotify.errorhandling.utils;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExceptionMessage {
    String title;
    Integer status;
    String details;

}
