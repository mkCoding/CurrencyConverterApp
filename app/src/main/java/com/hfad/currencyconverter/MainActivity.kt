package com.hfad.currencyconverter


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var adapter:ArrayAdapter<*>
    private lateinit var fromSpinner: Spinner;
    private lateinit var toSpinner: Spinner;
    private lateinit var convertButton:ExtendedFloatingActionButton;
    private lateinit var currencySymbol:TextView;
    private lateinit var editCurrencyAmount:EditText;
    private lateinit var amountResult:TextView;




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        convertButton = findViewById(R.id.convertButton);
        currencySymbol = findViewById(R.id.currencySymbol);
        editCurrencyAmount = findViewById(R.id.editCurrencyAmount);
        amountResult = findViewById(R.id.resultCurrencyView);

        //populate the listView
       var currencyTypes = resources.getStringArray(R.array.currency_array)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currencyTypes)
        fromSpinner.adapter = adapter;


        //If item is selected in FROM dropdown
        fromSpinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                //display the selected currency
               Toast.makeText(applicationContext,"selected currency "+currencyTypes[position], Toast.LENGTH_SHORT).show();

                when(currencyTypes[position]){
                    "USD" -> {editCurrencyAmount.hint = "Dollar Amount";
                              currencySymbol.text = "$";
                    }
                    "EUR" -> { editCurrencyAmount.hint = "Euro Amount";
                               currencySymbol.text = "€"
                    }
                    "JPY" -> {editCurrencyAmount.hint = "Yen Amount";
                               currencySymbol.text = "¥"
                    }
                    "GBP" -> {editCurrencyAmount.hint = "Pound Amount";
                        currencySymbol.text = "£";
                    }
                    "CHF" -> {editCurrencyAmount.hint = "Swiss Franc Amount";
                        currencySymbol.text = "CHF";}
                    "CAD" -> {editCurrencyAmount.hint = "Canadian Dollar Amount";
                         currencySymbol.text = "C$"}

                    "AUD" -> {editCurrencyAmount.hint = "Australian Dollar Amount";
                               currencySymbol.text = "A$"}

                    "NZD" -> {editCurrencyAmount.hint = "New Zealand Dollar Amount";
                              currencySymbol.text="NZ$"}
                    "ZAR" -> {editCurrencyAmount.hint = "South African Rand Amount";
                        currencySymbol.text="R"}


                }
            }


            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }



        //call this method to convert from 1 currency to another
        convertCurrency(convertButton,fromSpinner,toSpinner,editCurrencyAmount);


        }
    private val client = OkHttpClient()

    private fun convertCurrency (button: MaterialButton, fromSpinner: Spinner, toSpinner: Spinner, editCurrencyAmount: EditText){
        button.setOnClickListener {

            //hide the keyboard on button click
            hideKeyboard();


            //Initialize values for from/To currency
            val fromCurrency = fromSpinner.selectedItem.toString();
            val toCurrency = toSpinner.selectedItem.toString();

            //Grab the currency symbol to be displayed in the result
            var resultCurrencySymbol = "";

            //this will check the currency in the to spinner and set the associated result currency symbol
            when(toCurrency){
                "USD" -> { resultCurrencySymbol = "$"; }
                "EUR" -> { resultCurrencySymbol = "€" }
                "JPY" -> { resultCurrencySymbol = "¥" }
                "GBP" -> { resultCurrencySymbol = "£";}
                "CHF" -> { resultCurrencySymbol = "₣";}
                "CAD" -> { resultCurrencySymbol = "C$"}
                "AUD" -> { resultCurrencySymbol = "A$"}
                "NZD" -> { resultCurrencySymbol="NZ$"}
                "ZAR" -> { resultCurrencySymbol="R"}
            }



            //If the value in edit currency amount is not empty then make a request to the REST API
            if(!editCurrencyAmount.equals("")){
                Thread {
                    try {
                        // Your implementation goes here
                        val request = Request.Builder()
                            .url("https://api.apilayer.com/exchangerates_data/convert?to=$toCurrency&from=$fromCurrency&amount=${editCurrencyAmount.text}")
                            .header("apiKey",BuildConfig.API_KEY_NAME)
                            .build()

                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            for ((name, value) in response.headers) {
                                println("$name: $value")
                            }

                        //println(response.body!!.string()) //prints entire response body


                            val jsonObject = JSONObject(response.body!!.string())

                            val result = jsonObject.get("result")
                            val roundResult = String.format(Locale.US, "%.2f", result)

//                            Log.d("Result:", "The conversion from USD to NGN is $roundResult");

                            //display the converted result to the screen after Convert button is presses
                            amountResult.text = "$resultCurrencySymbol $roundResult";

                        }




                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }.start()
            }else{
                //Do not convert the number
            }

//
//            Toast.makeText(applicationContext, "From currency: $fromCurrency",Toast.LENGTH_SHORT).show();
//            Toast.makeText(applicationContext,  "To Currency: $toCurrency",Toast.LENGTH_SHORT).show();





            }

        }



private fun hideKeyboard(){
        //hide the keyboard on button click
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: java.lang.Exception) {
            // TODO: handle exception
        }
    }



}




