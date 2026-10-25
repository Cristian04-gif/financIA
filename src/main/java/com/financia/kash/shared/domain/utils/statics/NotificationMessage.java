package com.financia.kash.shared.domain.utils.statics;

import java.util.Map;

import com.financia.kash.shared.domain.Notification;

public class NotificationMessage {

    public static enum NotificationStatus {
        SUCCESS, INFO, ERROR
    }

    public final static String TRANSFER_HEADER = "Transferencia Exitosa";
    public final static String TRANSFER_BODY = "La transferenca se agrego a su resumen de movimientos correctamente";

    public static Notification sendTransferNotification(Map<String, Object> data) {
        return Notification.of(NotificationStatus.SUCCESS.name(), TRANSFER_HEADER, TRANSFER_BODY, data);
    }
}
