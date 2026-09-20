package tn.IIT.mentorat_platform.service;

import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.request.LoginRequest;
import tn.IIT.mentorat_platform.dto.request.RegisterRequest;
import tn.IIT.mentorat_platform.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request, MultipartFile cvFile);
    AuthResponse login(LoginRequest request);
}
