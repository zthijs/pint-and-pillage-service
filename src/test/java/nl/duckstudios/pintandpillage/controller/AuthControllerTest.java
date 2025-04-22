package nl.duckstudios.pintandpillage.controller;

import nl.duckstudios.pintandpillage.Exceptions.ForbiddenException;
import nl.duckstudios.pintandpillage.Exceptions.UnmetEmailRequirementsException;
import nl.duckstudios.pintandpillage.Exceptions.UnmetPasswordRequirementsException;
import nl.duckstudios.pintandpillage.Exceptions.UserAlreadyExistsException;
import nl.duckstudios.pintandpillage.config.JwtTokenUtil;
import nl.duckstudios.pintandpillage.dao.UserDAO;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.model.JwtResult;
import nl.duckstudios.pintandpillage.model.LoginCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtTokenUtil jwtUtil;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        this.testUser = new User();
        this.testUser.setEmail("john.doe@outlook.com");
        this.testUser.setPassword("#FjYL%mLR41%sGIoodKLZ0jVB1b&BM");
    }

    @Test
    void when_registerUserWithInvalidEmail_then_throwUnmetEmailRequirementsException() {
        this.testUser.setEmail("invalid");

        assertThrows(UnmetEmailRequirementsException.class, () -> authController.register(this.testUser));
    }

    @Test
    void when_registerUserWithWeakPassword_then_throwUnmetPasswordRequirementsException() {
        this.testUser.setPassword("slecht");

        assertThrows(UnmetPasswordRequirementsException.class, () -> authController.register(this.testUser));
    }

    @Test
    void when_registerUserWithExistingEmail_then_throwUserAlreadyExistsException() {
        when(userDAO.findByEmail(this.testUser.getEmail())).thenReturn(Optional.of(this.testUser));

        assertThrows(UserAlreadyExistsException.class, () -> authController.register(this.testUser));
    }

    @Test
    void when_loginFirstTime_then_returnJwtResultWithFirstTimeLoggedInTrue() {
        LoginCredentials validLoginCredentials = new LoginCredentials(this.testUser.getEmail(), this.testUser.getPassword());
        mockLogin(validLoginCredentials);

        JwtResult jwtResult = authController.login(validLoginCredentials);

        assertTrue(jwtResult.isFirstTimeLoggedIn);
    }

    @Test
    void when_loginSecondTime_then_returnJwtResultWithFirstTimeLoggedInFalse() {
        LoginCredentials validLoginCredentials = new LoginCredentials(this.testUser.getEmail(), this.testUser.getPassword());
        mockLogin(validLoginCredentials);

        authController.login(validLoginCredentials);
        JwtResult jwtResult = authController.login(validLoginCredentials);

        assertFalse(jwtResult.isFirstTimeLoggedIn);
    }

    @Test
    void when_loginWithInvalidCredentials_then_throwForbiddenException() {
        LoginCredentials invalidLoginCredentials = new LoginCredentials("wrong@outlook.com", "wrong");

        when(authManager.authenticate(
                new UsernamePasswordAuthenticationToken(invalidLoginCredentials.username, invalidLoginCredentials.password)
        )).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(ForbiddenException.class, () -> authController.login(invalidLoginCredentials));
    }

    private void mockLogin(LoginCredentials loginCredentials) {
        String returnToken = "jwt.token.string";
        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.username, loginCredentials.password))).thenReturn(null);
        when(jwtUtil.generateToken(loginCredentials.username)).thenReturn(returnToken);
        when(userDAO.findByEmail(loginCredentials.username)).thenReturn(Optional.of(this.testUser));
    }
}