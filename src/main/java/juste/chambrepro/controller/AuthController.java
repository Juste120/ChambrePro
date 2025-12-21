package juste.chambrepro.controller;

import jakarta.validation.Valid;
import juste.chambrepro.dto.requests.ClientRequest;
import juste.chambrepro.dto.requests.LoginRequest;
import juste.chambrepro.dto.responses.ClientResponse;
import juste.chambrepro.dto.responses.JwtResponse;
import juste.chambrepro.service.AuthService;
import juste.chambrepro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ClientService clientService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse register(@Valid @RequestBody ClientRequest request) {
        return clientService.createClient(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}