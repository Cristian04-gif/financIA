package com.financia.kash.auth.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.input.Confirm2FARequestUseCase;
import com.financia.kash.auth.application.port.input.Request2faUseCase;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.application.port.output.UserSaveForAuthPort;
import com.financia.kash.auth.domain.exception.InvalidTwoFactorCodeException;
import com.financia.kash.auth.domain.model.TwoFactorAuth;
import com.financia.kash.shared.infrastructure.utils.event.DomainEventPublisher;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService implements Request2faUseCase, Confirm2FARequestUseCase {

    private final UserEmailForAuthenticationPort emailForAuthenticationPort;
    private final UserSaveForAuthPort saveForAuthPort;
    private final TwoFactorAuth twoFactorAuth;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Mono<Map<String, String>> setup2fa(String email) {
        return emailForAuthenticationPort.findByEmail(email).flatMap(user -> {
            String secret = twoFactorAuth.generateNewSecret();
            user.setSecret2fa(secret);
            String qrUrl = twoFactorAuth.getQRBarcodeURL(secret, email);

            return saveForAuthPort.save(user).then(twoFactorAuth.generateQRCodeBase64Reactive(qrUrl)).map(qrBase64 -> {
                Map<String, String> response = Map.of("qrImage", "data:image/png;base64," + qrBase64);

                return response;
            });

        });
    }

    @Override
    public Mono<String> confirm2fa(String emailUser, Map<String, String> request) {
        return emailForAuthenticationPort.findByEmail(emailUser).flatMap(user -> {

            String code = request.get("code");
            if (code == null || !code.matches("\\d{6}")) {
                return Mono.error(new InvalidTwoFactorCodeException());
            }

            if (!twoFactorAuth.verifyCode(user.getSecret2fa(), code)) {
                return Mono.error(new InvalidTwoFactorCodeException());
            }

            user.enable2fa();
            return saveForAuthPort.save(user).then(Mono.just("2FA activado existosamente"));
        });

    }

}
