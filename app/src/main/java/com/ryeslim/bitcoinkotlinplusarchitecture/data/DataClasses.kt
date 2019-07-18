package com.ryeslim.bitcoinkotlinplusarchitecture.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)

data class TheCurrency (val code: String, val symbol: String, val rate: String, val description: String, val rateFloat: Float)

data class TheTime (val updated: String, val updatedISO: String, val updateduk: String)

data class TheBPI (val USD: TheCurrency, val GBP: TheCurrency, val EUR: TheCurrency)

data class TheQuery(val time: TheTime, val disclaimer: String, val chartName: String, val bpi: TheBPI)
