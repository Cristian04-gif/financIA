package com.financia.kash.movimiento.suscripcion.domain.enums;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class SubscriptionFrequencyWriteConverter extends EnumWriteSupport<SubscriptionFrequency> {

}
