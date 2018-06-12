package com.example.daniellecoulter.ad340_coulter

import org.junit.Assert
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat

class InputValidtionTest {

    private val main = MainActivity()

    @Test
    fun validateInput_isTrue() {
        assertThat(
                main.inputIsValid("Test"), `is` (true))
    }

    @Test
    internal fun validateInput_Empty() {
        assertThat(main.inputIsValid(""), `is` (false))
    }

    @Test
    internal fun validateInput_Numeric() {
        assertThat(main.inputIsValid("98765"), `is` (false))
    }
}
