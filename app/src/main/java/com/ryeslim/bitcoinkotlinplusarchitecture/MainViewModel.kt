package com.ryeslim.bitcoinkotlinplusarchitecture

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.text.Html
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.ryeslim.bitcoinkotlinplusarchitecture.data.TheBPI
import com.ryeslim.bitcoinkotlinplusarchitecture.data.TheCurrency
import com.ryeslim.bitcoinkotlinplusarchitecture.data.TheQuery
import com.ryeslim.bitcoinkotlinplusarchitecture.data.TheTime
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.text.ParseException

class MainViewModel : ViewModel() {

    lateinit var number: NumberFormat

    var lastQuery: Long = 0
    var currentQuery: Long = 0

    val rateFloat: FloatArray? = FloatArray(NUMBER_OF_CURRENCIES)

    val chartName = MutableLiveData<String>()
    val localTime = MutableLiveData<String>()
    val rate1 = MutableLiveData<String>()
    val rate2 = MutableLiveData<String>()
    val rate3 = MutableLiveData<String>()

    init {
        chartName.value = ""
        localTime.value = ""
        rate1.value = ""
        rate2.value = ""
        rate3.value = ""
    }

    private var loadingQue: RequestQueue? = null

    fun setLoadingQue(loadingQue: RequestQueue) {
        this.loadingQue = loadingQue

        val arrReq = JsonObjectRequest(
            Request.Method.GET, "https://api.coindesk.com/v1/bpi/currentprice.json",
            object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {
                    if (response.length() > 0) {
                        try {
                            val jsonObject1 = response.getJSONObject("time")

                            val theTime = TheTime(
                                jsonObject1.getString("updated"),
                                jsonObject1.getString("updatedISO"), jsonObject1.getString("updateduk")
                            )

                            val theDisclaimer = response.getString("disclaimer")
                            val theChartName = response.getString("chartName")

                            val jsonObject2 = response.getJSONObject("bpi")

                            val dollarUS = TheCurrency(
                                jsonObject2.getJSONObject("USD").getString("code"),
                                jsonObject2.getJSONObject("USD").getString("symbol"),
                                jsonObject2.getJSONObject("USD").getString("rate"),
                                jsonObject2.getJSONObject("USD").getString("description"),
                                toFloat(jsonObject2.getJSONObject("USD").getString("rate"))
                            )

                            val poundUK = TheCurrency(
                                jsonObject2.getJSONObject("GBP").getString("code"),
                                jsonObject2.getJSONObject("GBP").getString("symbol"),
                                jsonObject2.getJSONObject("GBP").getString("rate"),
                                jsonObject2.getJSONObject("GBP").getString("description"),
                                toFloat(jsonObject2.getJSONObject("GBP").getString("rate"))
                            )

                            val euro = TheCurrency(
                                jsonObject2.getJSONObject("EUR").getString("code"),
                                jsonObject2.getJSONObject("EUR").getString("symbol"),
                                jsonObject2.getJSONObject("EUR").getString("rate"),
                                jsonObject2.getJSONObject("EUR").getString("description"),
                                toFloat(jsonObject2.getJSONObject("EUR").getString("rate"))
                            )

                            val bpi = TheBPI(dollarUS, poundUK, euro)
                            TheQuery(theTime, theDisclaimer, theChartName, bpi)

                            val dummyString = theTime.updated

                            chartName.value = theChartName
                            localTime.value = localTime(dummyString)
                            rate1.value = theLineToShow(dollarUS)
                            rate2.value = theLineToShow(poundUK)
                            rate3.value = theLineToShow(euro)
                            rateFloat?.set(0, toFloat(dollarUS.rate))
                            rateFloat?.set(1, toFloat(poundUK.rate))
                            rateFloat?.set(2, toFloat(euro.rate))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } finally {
                            return
                        }
                    }
                }
            },
            Response.ErrorListener { error -> println(error) }
        )
        loadingQue.add(arrReq)
    }

    fun localTime(updated: String): String {

        var localTime = ""
        val defaultTimeZone = TimeZone.getDefault()
        val strDefaultTimeZone = defaultTimeZone.getDisplayName(false, TimeZone.SHORT)
        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss z")
        simpleDateFormat.timeZone = TimeZone.getTimeZone(strDefaultTimeZone)

        try {
            localTime = simpleDateFormat.format(simpleDateFormat.parse(updated))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return localTime
    }

    fun toFloat(rate: String): Float {
        val rateString = rate.replace(",".toRegex(), "")
        return java.lang.Float.parseFloat(rateString)
    }

    fun theLineToShow(currencyObject: TheCurrency): String {
        var theLineToShow = ""
        val symbolToShow = Html.fromHtml(currencyObject.symbol).toString()
        theLineToShow = String.format("%s: %.4f", symbolToShow, currencyObject.rateFloat)
        return theLineToShow
    }

    fun divide(theValue: Float, theRate: Float?): String {
        val dummy: Float = theRate ?: 1.0F
        return number.format((theValue / dummy).toDouble()).replace(",".toRegex(), "")
    }

    fun multiply(theValue: Float, theRate: Float?): String {
        val dummy: Float = theRate ?: 1.0F
        return number.format((theValue * dummy).toDouble()).replace(",".toRegex(), "")
    }
}