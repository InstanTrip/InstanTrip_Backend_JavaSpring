package com.instantrip.instantrip_backend.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Cognito has a custom logout url.
 * See more information <a href="https://docs.aws.amazon.com/cognito/latest/developerguide/logout-endpoint.html">here</a>.
 */
@Component
public class CognitoLogoutHandler extends SimpleUrlLogoutSuccessHandler {

  @Value("${cognito.domain}")
  private String domain;

  @Value("${cognito.logout-redirect-url}")
  private String logoutRedirectUrl;

  @Value("${cognito.user-pool-client-id}")
  private String userPoolClientId;

  /**
   * Here, we must implement the new logout URL request. We define what URL to send our request to,
   * and set out client_id and logout_uri parameters.
   */
  @Override
  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    return UriComponentsBuilder
        .fromUri(URI.create(domain + "/logout"))
        .queryParam("client_id", userPoolClientId)
        .queryParam("logout_uri", logoutRedirectUrl)
        .encode(StandardCharsets.UTF_8)
        .build()
        .toUriString();
  }
}