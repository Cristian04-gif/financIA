package com.financia.kash.auth.application.port.input.command;

public record Verify2FACommand(String preToken, String code) {

}
