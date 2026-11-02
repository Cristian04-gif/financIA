package com.financia.kash.auth.application.port.input.command;

import java.util.Map;

public record Confirm2FACommand(String emailUser, Map<String, String> request) {

}
