package com.tennisclub.reservations.validator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.tennisclub.reservations.validator.annotation.PhoneNumber;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PhoneNumberValidatorTest {

    @Autowired
    private PhoneNumberUtil phoneNumberUtil;

    @ParameterizedTest
    @ValueSource(strings = {
            "+44 20 7946 0958",
            "+61 2 9876 5432",
            "+91 9367 788 755",
            "+49 170 1234567",
            "+16 308 520 397"
    })
    public void isValid(String phoneNumber) {
        var validator = new PhoneNumberValidator(phoneNumberUtil);
        var constraintAnnotation = new AnnotationDescriptor.Builder<>(PhoneNumber.class).build().getAnnotation();
        validator.initialize(constraintAnnotation);

        assertThat(validator.isValid(phoneNumber, null)).isTrue();
    }

}
