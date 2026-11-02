package com.financia.kash.shared.domain.utils.statics;

import java.math.BigDecimal;
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

        public static Notification sendAccountStatusChangedNotification(String accountName, boolean isActive,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.INFO.name(), "Estado de Cuenta Actualizado",
                                String.format("La cuenta '%s' fue %s.", accountName,
                                                isActive ? "activada" : "desactivada"),
                                data);
        }

        public static Notification sendAccountDeletedNotification(String accountName, Map<String, Object> data) {
                return Notification.of(NotificationStatus.WARNING.name(), "Cuenta Eliminada",
                                String.format("La cuenta '%s' fue eliminada exitosamente.", accountName), data);
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

        public static Notification sendMovementCreatedNotification(String type, BigDecimal amount, String description,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Movimiento Registrado",
                                String.format("Se registró un %s por el monto de S/ %.2f (%s).", type.toLowerCase(),
                                                amount, description),
                                data);
        }

        public static Notification sendMovementDeletedNotification(String description, BigDecimal amount,
                        String accountName,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.INFO.name(), "Movimiento Eliminado",
                                String.format("El movimiento '%s' por S/ %.2f fue eliminado y el saldo fue restaurado a la cuenta '%s'.",
                                                description, amount, accountName),
                                data);
        }

        public static Notification sendSubscriptionCreatedNotification(String subscriptionName, BigDecimal amount,
                        String frequency,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Suscripción Registrada",
                                String.format("Se programó la suscripción a '%s' por S/ %.2f con frecuencia %s.",
                                                subscriptionName, amount, frequency),
                                data);
        }

        public static Notification sendSubscriptionStatusChangedNotification(String subscriptionName, boolean isActive,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.INFO.name(), "Estado de Suscripción Modificado",
                                String.format("La suscripción a '%s' ahora se encuentra %s.",
                                                subscriptionName, isActive ? "activa" : "pausada"),
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

        public static Notification sendObjectiveCreatedNotification(String objectiveName, BigDecimal targetAmount,
                        Map<String, Object> data) {
                return Notification.of(NotificationStatus.SUCCESS.name(), "Nueva Meta Financiera",
                                String.format("Meta '%s' creada con éxito con un objetivo de S/ %.2f.", objectiveName,
                                                targetAmount),
                                data);
        }

        public static Notification sendContributionObjectiveNotification(String objectiveName, BigDecimal amount,
                        Double progress, boolean isCompleted, Map<String, Object> data) {
                if (isCompleted) {
                        return Notification.of(NotificationStatus.SUCCESS.name(), "¡Meta Cumplida con Éxito!",
                                        String.format("¡Felicidades! Has completado el 100%% de tu meta '%s' con tu aporte de S/ %.2f.",
                                                        objectiveName, amount),
                                        data);
                }

                return Notification.of(NotificationStatus.INFO.name(), "Aporte a Meta Registrado",
                                String.format("Se aportó S/ %.2f a tu meta '%s'. Progreso actual: %.1f%%.",
                                                amount, objectiveName, progress),
                                data);
        }

        public static Notification sendTransferDeletedNotification(BigDecimal amount, Map<String, Object> data) {
                return Notification.of(NotificationStatus.INFO.name(), "Transferencia Anulada",
                                String.format("Se anuló la transferencia de S/ %.2f y los fondos fueron revertidos a la cuenta de origen.",
                                                amount),
                                data);
        }

        public static Notification sendObjectivesExpired(String name, Map<String, Object> data) {
                return Notification.of(NotificationStatus.INFO.name(), "Meta Financiera",
                                String.format("No se logro cumplir con la meta '%s' establecida", name), data);
        }
}
