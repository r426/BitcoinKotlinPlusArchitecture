package com.ryeslim.bitcoinkotlinplusarchitecture

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CalculatorWatcher(private val currencyField: Array<EditText>) {

    init {
        calculatorWatcher(currencyField)
    }

    private fun calculatorWatcher(currencyField: Array<EditText>) {

        for (ii in 0..1) {
            val j = (ii + 1) % 2
            currencyField[ii].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    if (currencyField[ii].text.toString().isNotEmpty()) {
                        currencyField[j].isFocusable = false
                    } else {
                        for (i in 0..1) {
                            currencyField[i].isFocusableInTouchMode = true
                            currencyField[i].isFocusable = true
                        }
                    }
                }
            })
        }
    }
}
