package com.financia.kash.chatIa.infrastructure.adapter.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.chatIa.domain.enums.MessageType;
import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.Getter;
import lombok.Setter;

@Table("mensajes_chat")
@Getter
@Setter
public class FinancialChatMessageEntity implements HasUuid {
    @Id
    UUID id;
    @Column(value = "conversacion_id")
    UUID conversationId;
    @Column(value = "tipo")
    MessageType type;
    @Column(value = "contenido")
    String content;
    @Column(value = "fecha_creacion")
    LocalDateTime creationDate;
}
