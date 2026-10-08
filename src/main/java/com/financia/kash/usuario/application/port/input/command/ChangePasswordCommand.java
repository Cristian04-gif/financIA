package com.financia.kash.usuario.application.port.input.command;

import java.util.UUID;

public record ChangePasswordCommand(UUID id, String newPassword) {

}
