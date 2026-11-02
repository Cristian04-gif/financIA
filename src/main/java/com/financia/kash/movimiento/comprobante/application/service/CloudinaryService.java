package com.financia.kash.movimiento.comprobante.application.service;

import java.io.SequenceInputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.financia.kash.movimiento.comprobante.application.port.input.SaveFileVoucherUseCase;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public class CloudinaryService implements SaveFileVoucherUseCase {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    @Override
    public Mono<String> saveFileVoucher(FilePart file) {
        return file.content()
                .map(dataBuffer -> dataBuffer.asInputStream(true))
                .reduce(SequenceInputStream::new)
                .flatMap(inputStream -> Mono.fromCallable(() -> {
                    byte[] fileBytes = inputStream.readAllBytes();
                    log.info(file.filename());
                    Map<?, ?> uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());
                    return (String) uploadResult.get("secure_url");
                })).subscribeOn(Schedulers.boundedElastic());
    }

}
