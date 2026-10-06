package com.financia.kash;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ai.chat.client.ChatClient;
import com.financia.kash.movimiento.comprobante.application.service.CloudinaryService;

@SpringBootTest
@ActiveProfiles("test")
class KashApplicationTests {

    @MockitoBean
    private ChatClient chatClient;

    @MockitoBean
    private CloudinaryService cloudinaryService;

	@Test
	void contextLoads() {
	}

}
