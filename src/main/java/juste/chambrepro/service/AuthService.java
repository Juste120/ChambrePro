package juste.chambrepro.service;

import juste.chambrepro.dto.requests.LoginRequest;
import juste.chambrepro.dto.responses.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
}