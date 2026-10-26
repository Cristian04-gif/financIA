package com.financia.kash.shared.domain.utils.statics;

import java.util.Map;

import com.financia.kash.shared.domain.Notification;

public class NotificationMessage {

        public static enum NotificationStatus {
                SUCCESS, INFO, WARNING
        }

        public static Notification sendNotificationSuccessfulTransfer(Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Transferencia Exitosa",
                                "La transferencia se agregó a su resumen de movimientos correctamente", data);
        }

        public static Notification sendAccountCreationNotification(Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Cuenta creada con éxito",
                                "Cuenta agregada a la lista del usuario", data);
        }

        public static Notification sendNotificationBudgetApproachesLimit(String name, Double percentage,
                        Map<String, Object> data) {
                if (percentage > 100.00) {
                        return Notification.of(NotificationStatus.WARNING.name(),
                                        String.format("Presupuesto Consumido: %s", name),
                                        String.format(
                                                        "Los gastos del presupuesto '%s' superan lo establecido inicialmente, actualice el límite o bloquee los movimientos por la categoría",
                                                        name),
                                        data);
                }

                return Notification.of(NotificationStatus.INFO.name(), String.format("Presupuesto Consumido: %s", name),
                                String.format(
                                                "Presupuesto consumido al %.2f %% del límite, tenga cuidado con sus próximos movimientos",
                                                percentage),
                                data);

        }

        public static Notification sendSubscriptionPaymentNotification(String subscriptionName, String accountName,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Suscripción Cobrada con Éxito",
                                String.format("La suscripción a '%s' se cobró de la cuenta '%s'", subscriptionName,
                                                accountName),
                                data);
        }

        public static Notification sendNotificationPaymentReceived(String subscriptionName, String accountName,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Movimiento frecuente recibido",
                                String.format("El movimiento '%s' se sumó a su cuenta '%s'", subscriptionName,
                                                accountName),
                                data);
        }
}
