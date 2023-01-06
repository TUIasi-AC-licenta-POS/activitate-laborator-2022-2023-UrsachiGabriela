package spotify.utils.errorhandling;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExceptionResponse{
    String error;
    Integer status;
    String details;

}
