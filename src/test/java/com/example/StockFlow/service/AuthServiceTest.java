//// java
//package com.example.StockFlow.service;
//
//import com.example.StockFlow.dto.request.LoginRequest;
//import com.example.StockFlow.dto.request.RegisterRequest;
//import com.example.StockFlow.dto.response.UserResponse;
//import com.example.StockFlow.entity.User;
//import com.example.StockFlow.exception.CustomException;
//import com.example.StockFlow.mapper.UserMapper;
//import com.example.StockFlow.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private AuthService authService;
//
//    @Test
//    void register_whenEmailExists_throwsCustomException() {
//        RegisterRequest req = mock(RegisterRequest.class);
//        when(req.getEmail()).thenReturn("exists@example.com");
//        when(userRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(new User()));
//
//        assertThrows(CustomException.class, () -> authService.register(req));
//        verify(userRepository).findByEmail("exists@example.com");
//        verifyNoMoreInteractions(userRepository);
//    }
//
//    @Test
//    void register_success_returnsAuthResponse_and_sessionAllowsGetCurrentUser() {
//        // Arrange
//        RegisterRequest req = mock(RegisterRequest.class);
//        when(req.getEmail()).thenReturn("new@example.com");
//        when(req.getPassword()).thenReturn("plainPass");
//
//        User toSave = new User();
//        when(userMapper.toEntity(req)).thenReturn(toSave);
//
//        UserResponse userResp = new UserResponse();
//        when(userMapper.toResponse(any(User.class))).thenReturn(userResp);
//
//        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
//        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
//
//        // Act
//        var auth = authService.register(req);
//
//        // Assert
//        assertNotNull(auth);
//        assertNotNull(auth.getSessionId());
//        // saved user should have password set and not equal raw password
//        User saved = captor.getValue();
//        assertNotNull(saved.getPassword());
//        assertNotEquals("plainPass", saved.getPassword());
//        // session works: getCurrentUser with returned token
//        var current = authService.getCurrentUser(auth.getSessionId());
//        assertNotNull(current);
//        assertEquals(auth.getSessionId(), current.getSessionId());
//        verify(userRepository).findByEmail("new@example.com");
//        verify(userRepository).save(any(User.class));
//        verify(userMapper, atLeast(1)).toResponse(any(User.class));
//    }
//
//    @Test
//    void login_whenEmailNotFound_throwsCustomException() {
//        LoginRequest req = mock(LoginRequest.class);
//        when(req.getEmail()).thenReturn("missing@example.com");
//        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
//
//        assertThrows(CustomException.class, () -> authService.login(req));
//        verify(userRepository).findByEmail("missing@example.com");
//    }
//
//    @Test
//    void login_success_allowsGetCurrentUser_and_logout_invalidatesSession() {
//        // Arrange
//        LoginRequest req = mock(LoginRequest.class);
//        when(req.getEmail()).thenReturn("user@example.com");
//
//        User existing = new User();
//        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existing));
//
//        UserResponse userResp = new UserResponse();
//        when(userMapper.toResponse(existing)).thenReturn(userResp);
//
//        // Act - login
//        var auth = authService.login(req);
//
//        // Assert login returned token and can get current user
//        assertNotNull(auth);
//        String token = auth.getSessionId();
//        assertNotNull(token);
//
//        var current = authService.getCurrentUser(token);
//        assertNotNull(current);
//        assertEquals(token, current.getSessionId());
//
//        // Act - logout then getCurrentUser should fail
//        authService.logout(token);
//        assertThrows(CustomException.class, () -> authService.getCurrentUser(token));
//
//        verify(userRepository, times(1)).findByEmail("user@example.com");
//        verify(userMapper, atLeast(1)).toResponse(existing);
//    }
//}
