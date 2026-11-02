package com.financia.kash.cuenta.cuenta.domain.enums;

import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

@WritingConverter
public class AccountTypeWriteConverter extends EnumWriteSupport<AccountType> {

}
