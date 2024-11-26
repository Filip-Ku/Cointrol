package com.example.cointrol.ui.savings

import NbpApiService
import NbpExchangeRate
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointrol.MyAdapter
import com.example.cointrol.databinding.FragmentSavingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cointrol.ui.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.random.*

class SavingsFragment : Fragment() {

    private var _binding: FragmentSavingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pln: TextView
    private lateinit var dollar: TextView
    private lateinit var plnEdit: EditText
    private lateinit var dollarEdit: EditText
    private lateinit var currencies: TextView
    private lateinit var lastIncomes: ArrayList<Transaction>
    private lateinit var lastOutcomes: ArrayList<Transaction>
    private lateinit var incomeAdapter: MyAdapter
    private lateinit var outcomeAdapter: MyAdapter


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        pln = binding.pln
        dollar = binding.dollar
        plnEdit = binding.plnEdit
        dollarEdit = binding.dollarEdit
        currencies = binding.sumOfCurrencies
        fetchExchangeRate("usd")

        val sharedPreferences = requireContext().getSharedPreferences("SavingsPrefs", Context.MODE_PRIVATE)
        val savedPlnValue = sharedPreferences.getString("pln_value", "0 PLN")
        val savedDollarValue = sharedPreferences.getString("dollar_value", "0 $")

        lastOutcomes = ArrayList()
        outcomeAdapter = MyAdapter(lastOutcomes, requireContext())
        binding.outcomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.outcomeRecyclerView.adapter = outcomeAdapter



        lastIncomes = ArrayList()
        incomeAdapter = MyAdapter(lastIncomes, requireContext())
        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeRecyclerView.adapter = incomeAdapter

        val db = TransactionDatabase.getDatabase(requireContext())


        loadLastTransactions(db)

        pln.text = savedPlnValue
        dollar.text = savedDollarValue

        binding.coinImageView.setOnClickListener { view ->
            changeBallance(view)
        }

        return root
    }

    object RetrofitClient {
        private const val BASE_URL = "https://api.nbp.pl/api/"
        val instance: NbpApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NbpApiService::class.java)
        }
    }

    override fun onPause() {
        val sharedPreferences = requireContext().getSharedPreferences("SavingsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("pln_value", pln.text.toString())
        editor.putString("dollar_value", dollar.text.toString())
        editor.apply()
        super.onPause()
    }

    fun changeBallance(view: View) {
        val isEditing = pln.visibility == View.VISIBLE

        pln.visibility = if (isEditing) View.INVISIBLE else View.VISIBLE
        dollar.visibility = if (isEditing) View.INVISIBLE else View.VISIBLE
        plnEdit.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE
        dollarEdit.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE

        if (isEditing) {
            plnEdit.setText(Regex("\\d+").find(pln.text.toString())?.value)
            dollarEdit.setText(Regex("\\d+").find(dollar.text.toString())?.value)
        } else {
            pln.text = "${plnEdit.text} PLN"
            dollar.text = "${dollarEdit.text} $"
            fetchExchangeRate("usd")
        }
    }

    private fun loadLastTransactions(database: TransactionDatabase) {
        lifecycleScope.launch {
            val dao = database.transactionDao()
            val lastTransactions = withContext(Dispatchers.IO) {
                dao.getLastThreeIncomes()
            }
            lastIncomes.clear()
            lastIncomes.addAll(lastTransactions)
            incomeAdapter.notifyDataSetChanged()

            val lastOutcomesTransactions = withContext(Dispatchers.IO) {
                dao.getLastThreeOutcomes()
            }
            lastOutcomes.clear()
            lastOutcomes.addAll(lastOutcomesTransactions)

            binding.outcomeRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun fetchExchangeRate(currency: String) {
        val api = RetrofitClient.instance

        api.getExchangeRate(currency).enqueue(object : Callback<NbpExchangeRate> {
            override fun onResponse(
                call: Call<NbpExchangeRate>,
                response: Response<NbpExchangeRate>
            ) {
                if (response.isSuccessful) {
                    val exchangeRate = response.body()
                    exchangeRate?.let {
                        val dollarCount = Regex("\\d+(\\.\\d+)?").find(dollar.text.toString())?.value?.toFloat() ?: 0f
                        val plnCount = Regex("\\d+(\\.\\d+)?").find(pln.text.toString())?.value?.toFloat() ?: 0f

                        val dollarToPln = it.rates[0].mid.toFloat() * 0.98f * dollarCount

                        val sum = dollarToPln + plnCount
                        val formattedDollarToPln = String.format("%.2f", dollarToPln)
                        val formattedSum = String.format("%.2f", sum)

                        dollar.setText(dollar.text.toString()+" ("+formattedDollarToPln+" PLN)")
                        currencies.setText("$formattedSum PLN")
                    }
                } else {
                    Log.e("NBP_API", "Błąd: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NbpExchangeRate>, t: Throwable) {
                Log.e("NBP_API", "Błąd sieci: ${t.message}")
            }
        })
    }
}