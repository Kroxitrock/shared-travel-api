package eu.sharedtravel.app.components.user.controller;

import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.service.UserService;
import eu.sharedtravel.app.components.user.service.dto.ChangePasswordDto;
import eu.sharedtravel.app.components.user.service.dto.LoginDto;
import eu.sharedtravel.app.components.user.service.dto.RegisterDto;
import eu.sharedtravel.app.config.security.JWTGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "User Endpoints")
public class UserController {

    private final UserService userService;
    private final JWTGenerator jwtGenerator;

    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Login method")
    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginDto loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        return jwtGenerator.generateJWT((User) authenticate.getPrincipal());
    }

    @Operation(summary = "Register method")
    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterDto registerDto) {
        registerDto.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        String userJwt = userService.register(registerDto);

        log.info("Successfully registered user with email {}.", registerDto.getEmail());

        return userJwt;
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Change password method", security = @SecurityRequirement(name = "JWT"))
    @PatchMapping("/me/password")
    @SuppressWarnings("squid:S4684") // The user is coming from @ResolveUser and is thus safe
    public void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
        @ResolveUser User user) {
        if (!bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("The provided password did not match the logged-in user's credentials.");
        }

        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getPassword())) {
            throw new BadCredentialsException("New and Old credentials are the same!");
        }

        changePasswordDto.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getPassword()));

        userService.changePassword(changePasswordDto, user);
    }
}
