package com.msgs.global.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.error.ErrorCode;
import com.msgs.global.common.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (BusinessException e) {
      setErrorResponse(response, e.getErrorCode());
    }
  }

  private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
    response.setStatus(errorCode.getHttpStatus().value());

    ErrorResponse errorResponse = ErrorResponse.builder()
                                               .code(errorCode.name())
                                               .message(errorCode.getMessage())
                                               .build();

    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);

      String jsonResponse = mapper.writeValueAsString(errorResponse);
      response.getWriter().write(jsonResponse);

    } catch (IOException e) {
      log.error("Error writing response: {}", e.getMessage(), e);
    }
  }

}
