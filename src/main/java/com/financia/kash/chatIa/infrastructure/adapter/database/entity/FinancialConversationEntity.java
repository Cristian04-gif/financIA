package com.financia.kash.chatIa.infrastructure.adapter.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.financia.kash.shared.infrastructure.utils.HasUuid;

import lombok.Getter;
import lombok.Setter;

@Table("conversaciones_chat")
@Getter
@Setter
public class FinancialConversationEntity implements HasUuid {
    @Id
    private UUID id;
    @Column(value = "usuario_id")
    private UUID userId;
    @Column(value = "titulo")
    private String title;
    @Column(value = "activa")
    private Boolean active;
    @Column(value = "fecha_creacion")
    private LocalDateTime creationDate;
    @Column(value = "fecha_actualizacion")
    private LocalDateTime updateDate;
}
