package com.financia.kash.movimiento.movimiento.domain.enums;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class TypeMovementWriteConverter extends EnumWriteSupport<TypeMovement> {

}
