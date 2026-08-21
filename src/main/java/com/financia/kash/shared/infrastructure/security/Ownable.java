package com.financia.kash.shared.infrastructure.security;

import java.util.List;

public interface Ownable {
    List<String> getOwnerEmail();
}
