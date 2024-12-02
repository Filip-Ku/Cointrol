package com.example.cointrol.ui.outcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log

import android.widget.Button

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointrol.MyAdapter
import com.example.cointrol.database.Transaction
import com.example.cointrol.database.TransactionDao
import com.example.cointrol.database.TransactionDatabase
import com.example.cointrol.databinding.FragmentOutcomeBinding
import com.example.cointrol.ui.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class OutcomeFragment : Fragment() {

    private var _binding: FragmentOutcomeBinding? = null
    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var currency: Button
    private lateinit var lastOutcomes: ArrayList<Transaction>
    private lateinit var outcomeAdapter: MyAdapter
    private lateinit var highest: TextView
    private lateinit var combined: TextView
    private lateinit var choosenPeriod: String

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViews()
        setupClickListeners()
        setupRecyclerView()

        loadTransactions("allTime")

        return root
    }

    private fun initViews() {
        description = binding.descInput
        amount = binding.amountInput
        currency = binding.currency
        highest = binding.highestAmount
        combined = binding.combinedOutcome
        choosenPeriod="allTime"
    }

    private fun setupRecyclerView() {
        lastOutcomes = ArrayList()
        outcomeAdapter = MyAdapter(lastOutcomes, requireContext())
        binding.outcomeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = outcomeAdapter
            addItemDecoration(SpaceItemDecoration(16))
        }
    }

    private fun setupClickListeners() {
        binding.allTime.setOnClickListener { loadTransactions("allTime")
            changeButtonColor(it)}
        binding.lastMonth.setOnClickListener { loadTransactions("lastMonth")
            changeButtonColor(it)}
        binding.thisMonth.setOnClickListener { loadTransactions("thisMonth")
            changeButtonColor(it)}
        binding.addOutcome.setOnClickListener {
            addOutcomeFun()
            animateAddButton()
        }
        binding.magnifier.setOnClickListener {
            searchByPhrase()
        }
    }

    private fun changeButtonColor(view: View) {
        val selectedColor = Color.parseColor("#a8a8a8")
        val defaultColor = Color.parseColor("#ffffff")

        binding.allTime.setBackgroundColor(defaultColor)
        binding.thisMonth.setBackgroundColor(defaultColor)
        binding.lastMonth.setBackgroundColor(defaultColor)

        when(view.tag.toString()) {
            "allTime" -> {
                binding.allTime.setBackgroundColor(selectedColor)
                choosenPeriod = "allTime"
            }
            "thisMonth" -> {
                binding.thisMonth.setBackgroundColor(selectedColor)
                choosenPeriod = "thisMonth"
            }
            "lastMonth" -> {
                binding.lastMonth.setBackgroundColor(selectedColor)
                choosenPeriod = "lastMonth"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchByPhrase() {
        val phrase = binding.searchInput.text.toString().trim()
        if (phrase.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
            return
        }

        val db = TransactionDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val dao = db.transactionDao()

            val transactions = withContext(Dispatchers.IO) {
                when (choosenPeriod) {
                    "allTime" -> dao.searchByPhrase("outcome","%$phrase%")
                    else -> {
                        val (startDate, endDate) = getCalendarMonth(choosenPeriod)
                        dao.searchByPhraseForDateRange("outcome","%$phrase%", startDate, endDate)
                    }
                }
            }

            lastOutcomes.clear()
            lastOutcomes.addAll(transactions)
            outcomeAdapter.notifyDataSetChanged()
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun loadTransactions(period: String) {
        val db = TransactionDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val dao = db.transactionDao()

            val transactions = withContext(Dispatchers.IO) { getTransactionsByPeriod(dao, period, "outcome") }
            lastOutcomes.clear()
            lastOutcomes.addAll(transactions)
            outcomeAdapter.notifyDataSetChanged()

            val baseHighestAmount = withContext(Dispatchers.IO) { getHighestTransactionByPeriod(dao, period, "outcome") }
            highest.text = baseHighestAmount?.toString() ?: "0"

            val baseCombinedOutcome = withContext(Dispatchers.IO) { getCombinedTransactionByPeriod(dao, period, "outcome") }
            combined.text = baseCombinedOutcome?.toString() ?: "0"
        }
    }

    private suspend fun getTransactionsByPeriod(dao: TransactionDao, period: String, type: String): List<Transaction> {
        return when (period) {
            "allTime" -> dao.getAllTransactions(type)
            else -> {
                val (startDate, endDate) = getCalendarMonth(period)
                dao.getTransactionsForDateRange(type, startDate, endDate)
            }
        }
    }

    private suspend fun getHighestTransactionByPeriod(dao: TransactionDao, period: String, type: String): Double? {
        return when (period) {
            "allTime" -> dao.getHighestTransaction(type)
            else -> {
                val (startDate, endDate) = getCalendarMonth(period)
                dao.getHighestTransactionForDateRange(type, startDate, endDate)
            }
        }
    }

    private suspend fun getCombinedTransactionByPeriod(dao: TransactionDao, period: String, type: String): Double? {
        return when (period) {
            "allTime" -> dao.getCombinedTransaction(type)
            else -> {
                val (startDate, endDate) = getCalendarMonth(period)
                dao.getCombinedTransactionForDateRange(type, startDate, endDate)
            }
        }
    }

    private fun animateAddButton(){
        val scaleDown = ObjectAnimator.ofFloat(binding.addOutcome, "scaleX", 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(binding.addOutcome, "scaleY", 0.8f)

        val scaleUp = ObjectAnimator.ofFloat(binding.addOutcome, "scaleX", 1f)
        val scaleUpY = ObjectAnimator.ofFloat(binding.addOutcome, "scaleY", 1f)

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleDown).before(scaleUp)
        animatorSet.play(scaleDownY).before(scaleUpY)
        animatorSet.duration = 100
        animatorSet.start()
    }

    private fun addOutcomeFun() {
        val db = TransactionDatabase.getDatabase(requireContext())
        val descriptionText = description.text.toString()
        val amountText = amount.text.toString()

        if (descriptionText.isEmpty() || amountText.isEmpty()) {
            return
        }

        val amountValue = amountText.toDoubleOrNull() ?: return

        val currentDate = Date()
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormatter.format(currentDate)

        lifecycleScope.launch {
            val dao = db.transactionDao()

            val transaction = Transaction(
                type = "outcome",
                desc = descriptionText,
                amount = amountValue,
                date = formattedDate
            )

            dao.insert(transaction)
        }
        Toast.makeText(requireContext(),"Outcome added",Toast.LENGTH_SHORT).show()
        loadTransactions("allTime")
        editSharedPreferences(amountValue)
    }

    private fun editSharedPreferences(amount: Double){
        val sharedPreferences = requireContext().getSharedPreferences("SavingsPrefs", Context.MODE_PRIVATE)
        val plnValue = sharedPreferences.getString("pln_value", "0 PLN") ?: "0 PLN"

        val valueString = plnValue.replace(Regex("[^0-9.]"), "")
        val currentAmount = valueString.toDoubleOrNull() ?: 0.0

        val newAmount = currentAmount - amount

        val newPlnValue = "$newAmount PLN"

        val editor = sharedPreferences.edit()
        editor.putString("pln_value", newPlnValue)
        editor.apply()
    }

    private fun getCalendarMonth(month:String): Pair<String,String>{
        val calendar = Calendar.getInstance()
        if (month == "lastMonth") {
            calendar.add(Calendar.MONTH, -1)
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDay = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return Pair(formatter.format(firstDay), formatter.format(lastDay))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}