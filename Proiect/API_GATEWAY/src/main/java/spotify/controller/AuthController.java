package spotify.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spotify.clients.IDMClient;
import spotify.configs.IDMClientConfig;
import spotify.view.requests.LoginRequest;
import spotify.view.responses.LoginResponse;


//TODO
// logs


//@CrossOrigin(origins = "http://localhost:4200")

@Log4j2
@RestController
@Validated
@RequestMapping(value = "/api/spotify", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final IDMClient idmClient = new AnnotationConfigApplicationContext(IDMClientConfig.class).getBean(IDMClient.class);


    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        log.info("[{}] -> POST, login: request:{}", this.getClass().getSimpleName(), loginRequest);
        LoginResponse response = idmClient.login(loginRequest);

        if (response.getJwsToken().equals("False")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("logout")
    public ResponseEntity<Object> logout(@RequestBody LoginResponse logoutRequest) {

        log.info("[{}] -> DELETE, logout: request:{}", this.getClass().getSimpleName(), logoutRequest);
        boolean response = idmClient.logout(logoutRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}






























