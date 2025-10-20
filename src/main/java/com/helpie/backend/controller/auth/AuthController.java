package com.helpie.backend.controller.auth;


import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.dto.auth.HttpSigninInResponse;
import com.helpie.backend.dto.auth.ResponseCookie;
import com.helpie.backend.dto.auth.TokenRenewRequest;
import com.helpie.backend.dto.global.Response;
import com.helpie.backend.dto.sociallogin.SigninByCodeRequest;
import com.helpie.backend.dto.sociallogin.SignupByCodeRequest;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.service.auth.AuthService;
import com.helpie.backend.service.sociallogin.SocialLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {
    private final SocialLoginService socialLoginService;
    private final AuthService authService;

    @PostMapping("/social-login/{socialType}/signin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = HttpSigninInResponse.class)
            ))
    })
    @Operation(summary = "social auth 서버에서 발급된 code로 로그인을 합니다.")
    public HttpSigninInResponse signin(
            @PathVariable("socialType") SocialType socialType,
            @RequestBody
            @Valid
            SigninByCodeRequest signinByCodeRequest,
            HttpServletResponse response
    ) {
        final var userVo = this.socialLoginService.findUserVoByCode(
                socialType,
                signinByCodeRequest.code(),
                signinByCodeRequest.redirectUri()
        );

        final var token = this.authService.signin(userVo.getId());

        this.setRefreshTokenCookie(response, token.refreshToken());

        return new HttpSigninInResponse(token.accessToken(), token.refreshToken());
    }

    @PostMapping("/social-login/{socialType}/signup")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = HttpSigninInResponse.class)
            ))
    })
    @Operation(summary = "social auth 서버에서 발급한 accessToken으로 회원가입을 합니다.")
    public HttpSigninInResponse signup(
            @PathVariable("socialType") SocialType socialType,
            @RequestBody
            @Valid
            SignupByCodeRequest signupByCodeRequest,
            HttpServletResponse response
    ) {
        final var token = this.authService.signup(
                socialType,
                signupByCodeRequest
        );
        this.setRefreshTokenCookie(response, token.refreshToken());

        return new HttpSigninInResponse(token.accessToken(), token.refreshToken());
    }

    @PostMapping("/token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = HttpSigninInResponse.class)
            ))
    })
    @Operation(
            summary = "refreshToken으로 accessToken을 재발급합니다.",
            description = "만약, refreshToken 만료 3일전이라면 refreshToken도 재발급됩니다."
    )
    public HttpSigninInResponse generateAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response,
            @Valid @RequestBody() TokenRenewRequest tokenRenewRequest
    ) {
        try {
            if (refreshToken == null) {
                refreshToken = tokenRenewRequest.refreshToken();
            }

            if (refreshToken == null) {
                throw new RuntimeException("토큰이 없습니다.");
            }

            final var rt = this.authService.generateRefreshTokenOrEmpty(
                    refreshToken
            ).orElse(refreshToken);

            final var accessToken = this.authService.generateAccessToken(rt);

            this.setRefreshTokenCookie(
                    response,
                    rt
            );

            return new HttpSigninInResponse(accessToken, rt);
        } catch (BusinessException exception) {
            if (
                    exception.getErrorCode() == ErrorCode.TOKEN_NOT_EXIST ||
                            exception.getErrorCode() == ErrorCode.TOKEN_EXPIRED_ERROR ||
                            exception.getErrorCode() == ErrorCode.NOT_FOUND_REFRESH_TOKEN
            ) {
                this.removeRefreshTokenCookie(response);
            }
            throw exception;
        }
    }

    @PostMapping("/signout")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "204"
            )
    )
    @Operation(summary = "로그아웃 API", description = "refresh token 제거")
    public Response<Void> logout(
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken != null) {
            this.authService.removeRefreshToken(refreshToken);
        }

        this.removeRefreshTokenCookie(response);

        return Response.success();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.builder()
                .refreshToken(refreshToken)
                .sameSite("None")
                .secure(true)
                .maxAge(14 * 24 * 60 * 60)
                .httpOnly(true)
                .path("/")
                .build();

        String cookieHeader = String.format("refreshToken=%s; Max-Age=%d; HttpOnly=%b; Secure=%b; Path=%s; SameSite=%s",
                cookie.refreshToken(),
                cookie.maxAge(),
                cookie.httpOnly(),
                cookie.secure(),
                cookie.path(),
                cookie.sameSite());

        response.addHeader("Set-Cookie", cookieHeader);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.builder()
                .refreshToken(null)
                .sameSite("None")
                .secure(true)
                .maxAge(0)
                .httpOnly(true)
                .path("/")
                .build();

        String cookieHeader = String.format("refreshToken=%s; Max-Age=%d; HttpOnly=%b; Secure=%b; Path=%s; SameSite=%s",
                cookie.refreshToken(),
                cookie.maxAge(),
                cookie.httpOnly(),
                cookie.secure(),
                cookie.path(),
                cookie.sameSite());

        response.addHeader("Set-Cookie", cookieHeader);
    }
}
