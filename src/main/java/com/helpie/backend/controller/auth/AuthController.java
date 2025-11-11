package com.helpie.backend.controller.auth;


import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.domain.user.User;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.auth.*;
import com.helpie.backend.dto.global.Response;
import com.helpie.backend.dto.sociallogin.SigninByCodeRequest;
import com.helpie.backend.dto.sociallogin.SignupByCodeRequest;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.service.auth.AuthService;
import com.helpie.backend.service.auth.EmailService;
import com.helpie.backend.service.sociallogin.SocialLoginService;
import com.helpie.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {
    private final SocialLoginService socialLoginService;
    private final AuthService authService;
    private final EmailService emailService;
    private final UserService userService;

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

    @PostMapping("/signin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = HttpSigninInResponse.class)
            ))
    })
    @Operation(
            summary = "일반 로그인"
    )
    public HttpSigninInResponse signIn(@RequestBody @Valid SignInRequest signInRequest) {
        final var token = this.authService.signin(signInRequest);
        return new HttpSigninInResponse(token.accessToken(), token.refreshToken());
    }


    @PostMapping("/signup")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = HttpSigninInResponse.class)
            ))
    })
    @Operation(
            summary = "일반 회원가입"
    )
    public HttpSigninInResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        final var token = this.authService.signup(signUpRequest);
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
            @Valid @RequestBody TokenRenewRequest tokenRenewRequest
    ) {
        final var refreshToken = tokenRenewRequest.refreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_EXIST) {};
        }

        final var rotatedRt = authService.generateRefreshTokenOrEmpty(refreshToken);
        final var rt = rotatedRt.orElse(refreshToken);
        final var accessToken = authService.generateAccessToken(rt);

        return new HttpSigninInResponse(accessToken, rt);
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
            SignOutRequest signOutRequest
    ) {

        //TODO: 다중 로그인 케이스 추가 필요
        if (signOutRequest.refreshToken() != null) {
            this.authService.removeRefreshToken(signOutRequest.refreshToken());
        }

//        this.removeRefreshTokenCookie(response);

        return Response.success();
    }

    @PostMapping("/mail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = Response.class)
            ))
    })
    @Operation(
            summary = "이메일 인증 번호를 발송합니다."
    )
    public Response<String> mailSend(String mail) {
        return Response.success("" + emailService.sendAuthMail(mail));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = Response.class)
            ))
    })

     @Operation(
            summary = "이메일 인증에 대한 유효성을 검증합니다.",
            description = "인증번호, 만료여부를 확인합니다. 이메일 인증 성공 후 인증번호는 만료처리 됩니다."
    )
    @GetMapping("/mail-check")
    public Response<String> mailCheck(@RequestParam String mail, Integer authNumber) {
        return Response.success(emailService.checkValidAuthByEmail(mail, authNumber));
    }


    @PostMapping("/password-change")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(summary = "비밀번호 변경")
    public Response<String> passwordChange( @AuthenticationPrincipal UserVo userVo, PasswordChangeRequest passwordChangeRequest) {
        userService.updatePassword(userVo.getId(), passwordChangeRequest.password());
        return Response.success("비밀번호 변경이 완료되었습니다.");
    }


    @GetMapping("/username-check")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = Response.class)
            ))
    })

    @Operation(
            summary = "중복 닉네임 체크"
    )
    public Response<Boolean> usernameCheck(@RequestParam String username) {
        return Response.success(userService.existsByUsername(username));
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


    @GetMapping("/authorization-guide")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Authorization Guide - 로그인 or 회원가입 시 발급된 Access Token 으로 사용자 정보 조회"
            )
    )
    @Operation(summary = "엑세스 토큰으로 사용자 정보 조회 가이드")
    public Response<UserVo> authorizationGuide(
            @AuthenticationPrincipal UserVo userVo) {
        /**
         * @AuthenticationPrincipal : 어노테이션을 사용하여 현재 인증된 사용자의 정보를 가져올 수 있습니다.
         * @userVo.getId() : 현재 인증된 사용자의 고유 ID를 가져옵니다.
         **/
        final var user = this.userService.findUserVo(userVo.getId());
        return Response.success(user);
    }
}


