package io.tujh.imago.domain.auth

import java.util.LinkedList

interface Validator {
    fun validate(text: String): Boolean
}

fun validator(): Validator = TextValidator()

fun buildValidator(block: Validator.() -> Unit): Validator {
    val validator = TextValidator()
    validator.block()
    return validator
}

fun Validator.rule(isValid: String.() -> Boolean, invalidBlock: () -> Unit): Validator = withImpl {
    addRule(isValid, invalidBlock)
}

fun Validator.minLength(min: Int = 1, onInvalid: () -> Unit = {}) = rule(
    isValid = { length > min },
    invalidBlock = onInvalid
)

fun Validator.pattern(regex: Regex, onInvalid: () -> Unit = {}) = rule(
    isValid = { regex.matches(this) },
    invalidBlock = onInvalid
)

fun Validator.notBlank(onInvalid: () -> Unit = {}) = rule(
    isValid = String::isNotBlank,
    invalidBlock = onInvalid
)

private fun Validator.withImpl(block: TextValidator.() -> Unit): Validator {
    if (this is TextValidator) {
        block()
    }
    return this
}

private class TextValidator : Validator {
    private class Rule(val isValid: String.() -> Boolean, val invalidBlock: () -> Unit)
    private val rules = LinkedList<Rule>()

    fun addRule(isValid: String.() -> Boolean, invalidBlock: () -> Unit) {
        Rule(isValid, invalidBlock).let(rules::add)
    }

    override fun validate(text: String): Boolean {
        rules.forEach { rule ->
            if (rule.isValid(text).not()) {
                rule.invalidBlock()
                return false
            }
        }

        return true
    }
}