package com.financia.kash.meta.domain.enums;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class ObjectiveStateWriteConverter extends EnumWriteSupport<ObjectiveState> {

}
