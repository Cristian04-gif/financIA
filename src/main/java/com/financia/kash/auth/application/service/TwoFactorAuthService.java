package com.financia.kash.auth.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.input.Confirm2FARequestUseCase;
import com.financia.kash.auth.application.port.input.Request2faUseCase;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.domain.model.TwoFactorAuth;
import com.financia.kash.auth.infrastructure.adapter.event.Activation2FAEvent;
import com.financia.kash.auth.infrastructure.adapter.event.Request2faEvent;
import com.financia.kash.shared.infrastructure.utils.event.DomainEventPublisher;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService implements Request2faUseCase, Confirm2FARequestUseCase {

    private final UserEmailForAuthenticationPort emailForAuthenticationPort;
    private final TwoFactorAuth twoFactorAuth;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Mono<Map<String, String>> setup2fa(String email) {
        return emailForAuthenticationPort.findByEmail(email).flatMap(user -> {
            String secret = twoFactorAuth.generateNewSecret();
            user.setSecret2fa(secret);

            domainEventPublisher.publish(new Request2faEvent(user.getId(), secret));

            String qrUrl = twoFactorAuth.getQRBarcodeURL(secret, email);
            String qrBase64 = twoFactorAuth.generateQRCodeBase64(qrUrl);

            Map<String, String> response = Map.of("qrImage", "data:image/png;base64," + qrBase64);
            return Mono.just(response);

        });
    }

    @Override
    public Mono<String> confirm2fa(String emailUser, Map<String, String> request) {
        return emailForAuthenticationPort.findByEmail(emailUser).flatMap(user -> {
            String code = request.get("code");

            if (!twoFactorAuth.verifyCode(user.getSecret2fa(), code)) {
                return Mono.error(new RuntimeException("Codigo invalido, Intenta de nuevo"));
            }

            user.enable2fa();
            domainEventPublisher.publish(new Activation2FAEvent(user.getId()));
            return Mono.just("2FA activado existosamente");
        });

    }

}
