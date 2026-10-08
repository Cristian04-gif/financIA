package com.financia.kash.movimiento.comprobante.infrastructure.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.financia.kash.movimiento.comprobante.application.port.input.OcrUseCase;
import com.financia.kash.movimiento.comprobante.application.port.input.command.OrcProcessCommand;
import com.financia.kash.movimiento.comprobante.domain.model.Voucher;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final OcrUseCase ocrUseCase;

    @PostMapping(value = "/analyzer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Voucher>> analyzerVoucher(@AuthenticationPrincipal UserEntity user,
            @RequestPart("file") Mono<FilePart> filePart) {
        return filePart.flatMap(file -> {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.headers().getContentType().toString());

            return DataBufferUtils.join(file.content()).map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return bytes;
            }).flatMap(bytes -> ocrUseCase.OcrProcess(new OrcProcessCommand(user.getId(), bytes, mimeType)))
                    .map(ResponseEntity::ok);
        });
    }

}
