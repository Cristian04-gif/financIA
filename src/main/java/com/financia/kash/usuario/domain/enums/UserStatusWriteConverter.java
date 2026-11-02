package com.financia.kash.usuario.domain.enums;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class UserStatusWriteConverter extends EnumWriteSupport<UserStatus> {

}
