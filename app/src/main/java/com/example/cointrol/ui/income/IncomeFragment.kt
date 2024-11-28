package com.example.cointrol.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cointrol.databinding.FragmentIncomeBinding
import android.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator

import android.widget.ArrayAdapter
import android.widget.Button

import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointrol.MyAdapter
import com.example.cointrol.database.Transaction
import com.example.cointrol.database.TransactionDatabase
import com.example.cointrol.ui.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale


class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var currency: Button
    private lateinit var lastIncomes: ArrayList<Transaction>
    private lateinit var incomeAdapter: MyAdapter
    private lateinit var highest: TextView
    private lateinit var combined: TextView


    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        description=binding.descInput
        amount=binding.amountInput
        currency=binding.currency
        highest= binding.highestAmount
        combined=binding.combinedIncome


        binding.addIncome.setOnClickListener { view ->
            addIncomeFun(view)
            val scaleDown = ObjectAnimator.ofFloat(binding.addIncome, "scaleX", 0.8f)
            val scaleDownY = ObjectAnimator.ofFloat(binding.addIncome, "scaleY", 0.8f)

            val scaleUp = ObjectAnimator.ofFloat(binding.addIncome, "scaleX", 1f)
            val scaleUpY = ObjectAnimator.ofFloat(binding.addIncome, "scaleY", 1f)

            val animatorSet = AnimatorSet()
            animatorSet.play(scaleDown).before(scaleUp)
            animatorSet.play(scaleDownY).before(scaleUpY)
            animatorSet.duration = 100
            animatorSet.start()
        }

        lastIncomes = ArrayList()
        incomeAdapter = MyAdapter(lastIncomes, requireContext())
        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeRecyclerView.adapter = incomeAdapter
        binding.incomeRecyclerView.addItemDecoration(SpaceItemDecoration(16))


        loadLastTransactions()

        return root
    }

    private fun loadLastTransactions() {
        val db = TransactionDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val dao = db.transactionDao()
            val lastTransactions = withContext(Dispatchers.IO) {
                dao.getAllIncomes()
            }
            lastIncomes.clear()
            lastIncomes.addAll(lastTransactions)
            incomeAdapter.notifyDataSetChanged()

            val baseHighestAmount = withContext(Dispatchers.IO) {
                dao.getHighestIncome()
            }

            highest.text = baseHighestAmount?.toString() ?: "0"

            val baseCombinedIncome = withContext(Dispatchers.IO) {
                dao.getCombinedIncome()
            }

            combined.text = baseCombinedIncome?.toString() ?: "0"

             //dao.clearAllTransactions()
        }
    }

    private fun addIncomeFun(view: View) {
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
                type = "income",
                desc = descriptionText,
                amount = amountValue,
                date = formattedDate
            )

            dao.insert(transaction)
        }
        Toast.makeText(requireContext(),"Income added",Toast.LENGTH_SHORT).show()
        loadLastTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}