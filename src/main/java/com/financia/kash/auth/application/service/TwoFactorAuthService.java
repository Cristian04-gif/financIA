package com.financia.kash.auth.application.service;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.financia.kash.auth.application.port.input.Confirm2FARequestUseCase;
import com.financia.kash.auth.application.port.input.Request2faUseCase;
import com.financia.kash.auth.application.port.output.UserEmailForAuthenticationPort;
import com.financia.kash.auth.domain.model.TwoFactorAuth;
import com.financia.kash.auth.infrastructure.adapter.event.Activation2FAEvent;
import com.financia.kash.auth.infrastructure.adapter.event.Request2faEvent;
import com.financia.kash.usuario.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService implements Request2faUseCase, Confirm2FARequestUseCase {

    private final UserEmailForAuthenticationPort emailForAuthenticationPort;
    private final TwoFactorAuth twoFactorAuth;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Map<String, String> setup2fa(String email) {
        User user = emailForAuthenticationPort.findByEmail(email);

        String secret = twoFactorAuth.generateNewSecret();
        user.setSecret2fa(secret);
        eventPublisher.publishEvent(new Request2faEvent(user.getId(), secret));

        String qrUrl = twoFactorAuth.getQRBarcodeURL(secret, email);
        String qrBase64 = twoFactorAuth.generateQRCodeBase64(qrUrl);

        return Map.of("qrImage", "data:image/png;base64," + qrBase64);
    }

    @Override
    public String confirm2fa(String emailUser, Map<String, String> request) {
        User user = emailForAuthenticationPort.findByEmail(emailUser);

        String code = request.get("code");

        if (!twoFactorAuth.verifyCode(user.getSecret2fa(), code)) {
            throw new RuntimeException("Codigo invalido, Intenta de nuevo");
        }

        user.enable2fa();
        eventPublisher.publishEvent(new Activation2FAEvent(user.getId()));
        return "2FA activado existosamente";
    }

}
