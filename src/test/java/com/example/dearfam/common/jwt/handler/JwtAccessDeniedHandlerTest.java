package com.example.dearfam.common.jwt.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException exception;

    @InjectMocks
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 403
    @Test
    void handle_ShouldReturn403Forbidden() throws Exception {
        // given
//        AccessDeniedException exception = new AccessDeniedException("Forbidden Exception Test");

        // when
        jwtAccessDeniedHandler.handle(request, response, exception);

        // then
        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

}