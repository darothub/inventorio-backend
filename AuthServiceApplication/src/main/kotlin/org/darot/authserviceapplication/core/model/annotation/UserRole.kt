package org.darot.authserviceapplication.core.model.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.util.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [UserRoleValidator::class])
annotation class ValidUserRole(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "Invalid value for enum",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)


class UserRoleValidator : ConstraintValidator<ValidUserRole, String> {
    private lateinit var enumClass: Class<out Enum<*>>

    override fun initialize(annotation: ValidUserRole) {
        this.enumClass = annotation.enumClass.java
    }
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true
        return enumClass.enumConstants?.any { it.name == value.uppercase(Locale.getDefault()) } ?: false
    }
}