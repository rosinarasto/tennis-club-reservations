package com.tennisclub.reservations.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tennisclub.reservations.validator.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Autowired
    private final PhoneNumberUtil phoneNumberUtil;

    public PhoneNumberValidator(PhoneNumberUtil phoneNumberUtil) {
        this.phoneNumberUtil = phoneNumberUtil;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        log.info("Checking phone number {}", phoneNumber);

        try {
            var code = Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name();
            return phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phoneNumber, code));
        } catch (NumberParseException e) {
            return false;
        }
    }
}
