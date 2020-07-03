package com.arduia.myacc.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragExpenseEntryBinding
import com.arduia.myacc.ui.common.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException

class ExpenseEntryFragment : Fragment(){

    private val viewBinding by lazy {
        FragExpenseEntryBinding.inflate(layoutInflater)
    }

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
    }

    private fun setupView(){

        when{
            args.expenseId > 0 -> {
                // ID exist, Update Mode
                Timber.d("UpdateMode -> ${args.expenseId}")
                viewModel.setUpdateMode()
            }

            args.expenseId <= 0 -> {
                // ID is default, Save Mode
                Timber.d("SaveMode -> ${args.expenseId}")
                viewModel.setSaveMode()
            }
        }

        viewBinding.btnEntryClose.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.edtAmount.addTextChangedListener {
            viewBinding.btnSave.isEnabled = !it.isNullOrEmpty()
        }
    }


    private fun setupViewModel(){

        viewModel.dataInserted.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(viewBinding.root, "Data is Inserted", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        })

        viewModel.dataUpdated.observe(viewLifecycleOwner, EventObserver{
            Snackbar.make(viewBinding.root, "Data Updated", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        })

        viewModel.entryMode.observe(viewLifecycleOwner, EventObserver{
            when(it){
                ExpenseEntryMode.UPDATE -> {
                    onUpdateMode()
                }

                ExpenseEntryMode.INSERT -> {
                   onInsertMode()
                }
            }
        })

        viewModel.expenseData.observe(viewLifecycleOwner, Observer {
            viewBinding.edtName.setText(it.name)
            viewBinding.edtAmount.setText(it.amount)
            viewBinding.edtNote.setText(it.note)
        })
    }


    private fun onUpdateMode(){
        viewBinding.tvEntryTitle.text = getString(R.string.label_update)
        viewBinding.btnSave.text = getString(R.string.label_update)
        viewBinding.btnSave.setOnClickListener {
            updateData()
        }
        MainScope().launch(Dispatchers.IO){
            delay(0)
            viewModel.observeExpenseData(args.expenseId)
        }
    }

    private fun onInsertMode(){
        viewBinding.tvEntryTitle.text = getString(R.string.label_update)
        viewBinding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun saveData(){
        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        //View Level Validation
        if(cost.isEmpty()){
            viewBinding.edtAmount.error = "Cost is Empty"
            return
        }

        viewModel.saveExpenseData(
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = "Food"
        )

        clearSpendSheet()
    }

    private fun updateData(){

        val name = viewBinding.edtName.text.toString()
        val cost = viewBinding.edtAmount.text.toString()
        val description = viewBinding.edtNote.text.toString()

        //View Level Validation
        if(cost.isEmpty()){
            viewBinding.edtAmount.error = "Cost is Empty"
            return
        }

        viewModel.updateExpenseData(
            id = args.expenseId,
            name = name,
            cost = cost.toLongOrNull() ?: throw IllegalStateException("Entry cost is not a Decimal"),
            description = description,
            category = "Food"
        )

        clearSpendSheet()
    }


    private fun clearSpendSheet(){
        viewBinding.edtName.setText("")
        viewBinding.edtAmount.setText("")
        viewBinding.edtNote.setText("")
    }

    companion object{
        private const val TAG = "ExpenseUpdate"
    }


}
