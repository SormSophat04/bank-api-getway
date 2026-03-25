package com.lolc.api.getway.converter;

import com.lolc.api.getway.enums.BillType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BillTypeConverter implements Converter<String, BillType> {

    @Override
    public BillType convert(String source) {
        return BillType.valueOf(source.toUpperCase());
    }
}
