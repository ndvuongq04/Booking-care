package com.Booking_care.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Otp;
import com.Booking_care.domain.dto.AccountDTO.CreateAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.AuthDTO.PasswordDTO;
import com.Booking_care.domain.dto.AuthDTO.ReqLoginDTO;
import com.Booking_care.domain.dto.AuthDTO.ResLoginDTO;
import com.Booking_care.domain.dto.AuthDTO.ResetPasswordRequest;
import com.Booking_care.domain.dto.PatientDTO.ReqPatientDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.service.*;
import com.Booking_care.util.SecurityUtil;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.mapper.patient.PatientMapper;
import com.Booking_care.mapper.account.AccountMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final PatientService patientService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountService accountService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;

    @Value("${booking-care.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    private final String templateForgotPassword = "templateForgotPassword";
    private final String templateVerifyEmail = "templateVerifyEmail";

    public AuthController(AccountService accountService,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            OtpService otpService, PatientService patientService) {
        this.accountService = accountService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpService = otpService;
        this.patientService = patientService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // tạo authenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUserName(),
                loginDTO.getPassword());

        // xác thực người dùng -> loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        Account currentAcc = this.accountService.fetchAccountByEmail(loginDTO.getUserName());

        if (currentAcc != null) {

            Long patientId = (currentAcc != null && currentAcc.getPatient() != null)
                    ? currentAcc.getPatient().getId()
                    : null;

            Long actorId = null;
            String actorType = currentAcc.getRole().getName();

            switch (actorType) {
                case "CLIENT":
                    actorId = (currentAcc.getPatient() != null) ? currentAcc.getPatient().getId() : currentAcc.getId();
                    break;
                case "DOCTOR":
                    actorId = (currentAcc.getDoctor() != null) ? currentAcc.getDoctor().getId() : currentAcc.getId();
                    break;
                case "ADMIN":
                    actorId = currentAcc.getId();
                    break;
                case "SUPPORT":
                    actorId = (currentAcc.getSupport() != null) ? currentAcc.getSupport().getId() : currentAcc.getId();
                    break;
                default:
                    actorId = currentAcc.getId();
            }

            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentAcc.getId(),
                    currentAcc.getName(),
                    currentAcc.getEmail(),
                    currentAcc.getAvatar(),
                    currentAcc.getRole().getName().toUpperCase(),
                    actorType,
                    actorId);
            res.setUserLogin(userLogin);
        }

        // create token
        String accessToken = this.securityUtil.createAccessToken(currentAcc.getEmail(), res);
        res.setAccessToken(accessToken);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(currentAcc.getEmail(), res);

        // update refresh token
        this.accountService.updateToken(refresh_token, currentAcc.getEmail());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        Account currentAccountDB = this.accountService.fetchAccountByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        if (currentAccountDB != null) {
            userLogin.setId(currentAccountDB.getId());
            userLogin.setEmail(currentAccountDB.getEmail());
            userLogin.setName(currentAccountDB.getName());
            userLogin.setRole(currentAccountDB.getRole().getName().toString().toUpperCase());
        }

        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get Account by refresh token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email (validation inside service)
        Account currentAccount = this.accountService.getAccountByRefreshTokenAndEmail(refresh_token, email);

        // issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();
        Account currentAccountDB = this.accountService.fetchAccountByEmail(email);
        if (currentAccountDB != null) {

            Long patientId = (currentAccountDB != null && currentAccountDB.getPatient() != null)
                    ? currentAccountDB.getPatient().getId()
                    : null;
            Long actorId = null;
            String actorType = currentAccountDB.getRole().getName();

            switch (actorType) {
                case "CLIENT":
                    actorId = (currentAccountDB.getPatient() != null) ? currentAccountDB.getPatient().getId()
                            : currentAccountDB.getId();
                    break;
                case "DOCTOR":
                    actorId = (currentAccountDB.getDoctor() != null) ? currentAccountDB.getDoctor().getId()
                            : currentAccountDB.getId();
                    break;
                case "ADMIN":
                    actorId = currentAccountDB.getId();
                    break;
                case "SUPPORT":
                    actorId = (currentAccountDB.getSupport() != null) ? currentAccountDB.getSupport().getId()
                            : currentAccountDB.getId();
                    break;
                default:
                    actorId = currentAccountDB.getId();
            }

            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentAccountDB.getId(),
                    currentAccountDB.getEmail(),
                    currentAccountDB.getName(),
                    currentAccount.getAvatar(),
                    currentAccountDB.getRole().getName().toString().toUpperCase(),
                    actorType, actorId);
            res.setUserLogin(userLogin);

        }

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.accountService.updateToken(refresh_token, currentAccountDB.getEmail());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration) // sau maxAge -> token hết hạn
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout Account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        // Validation moved to service
        this.accountService.handleLogout(email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new account (send otp by email)")
    public ResponseEntity<String> register(@Valid @RequestBody CreateAccountDTO postManAccount) {
        // Validation in service
        this.accountService.validateEmailNotExists(postManAccount.getEmail());

        // send email
        this.emailService.sendEmailFromTemplateSync(postManAccount.getEmail(), "Xác thực email",
                templateVerifyEmail, postManAccount.getName(),
                this.otpService.generateOtp4Digits(postManAccount.getEmail()));

        return ResponseEntity.ok("sent otp by email, please call api create verify otp ");
    }

    @PutMapping("/auth/reset-password/{id}")
    @ApiMessage("Reset password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> resetPassword(@PathVariable long id, @RequestBody PasswordDTO passwordDTO) {
        // Validation in service
        this.accountService.handleResetPassword(id, passwordDTO.getNewPassword(), passwordDTO.getPassword());
        return ResponseEntity.ok(null);
    }

    @PostMapping("/auth/create-verify-otp")
    public ResponseEntity<ResPatientDTO> handleVerifyOtp(@RequestBody Otp otp) {

        // xác thực email (validation in service)
        otp.setCurrentSubmit(Instant.now());
        this.otpService.verifyOtpOrThrow(otp);

        // xóa OTP sau khi dùng xong
        this.otpService.invalidateOtp(otp.getEmail());

        // ok -> tạo
        CreateAccountDTO acc = new CreateAccountDTO();
        acc.setPassword(otp.getPassword());
        acc.setEmail(otp.getEmail());
        acc.setName(otp.getName());
        acc.setRoleId(4); // role patient

        Account a = this.accountService.handleCreateAccount(acc);

        // create patient
        ReqPatientDTO p = new ReqPatientDTO();
        p.setAccountId(a.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PatientMapper.toResPatientDTO(this.patientService.handleCreatePatient(p)));
    }

    @GetMapping("/auth/forgot-password-send-email")
    @ApiMessage("Forgot password (send otp by email)")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        // send email
        this.emailService.sendEmailFromTemplateSync(email, "Đặt lại mật khẩu",
                templateForgotPassword, null,
                this.otpService.generateOtp4Digits(email));

        return ResponseEntity.ok("sent otp by email, please call api forgot verify otp ");
    }

    @PostMapping("/auth/forgot-verify-otp")
    public ResponseEntity<String> handleVerifyOtpForgotPassword(@RequestBody Otp otp) {

        // xác thực email (validation in service)
        otp.setCurrentSubmit(Instant.now());
        this.otpService.verifyOtpOrThrow(otp);

        // xóa OTP sau khi dùng xong
        this.otpService.invalidateOtp(otp.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body("verify otp OK, please call api forgot password ");
    }

    @PostMapping("/auth/forgot-password")
    @ApiMessage("Forgot password")
    public ResponseEntity<ResAccountDTO> handleVerifyOtpForgotPassword(@RequestBody ResetPasswordRequest reset) {

        ResAccountDTO acc = AccountMapper.toResAccountDTO(
                this.accountService.forgotPassword(reset));

        return ResponseEntity.status(HttpStatus.OK)
                .body(acc);
    }

}
