package com.example.budgettracker.operations


import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentOperationsBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar

class OperationsFragment : Fragment() {

    private var _binding : FragmentOperationsBinding? = null
    private val binding get() = _binding!!
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{ AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy{ AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)}
    private val fromBottomBg: Animation by lazy{ AnimationUtils.loadAnimation(context, R.anim.from_bottom_bg_anim)}
    private val toBottom: Animation by lazy{ AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)}
    private var isExpanded = false
    private lateinit var deletedOperation : OperationsData

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOperationsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.total()
        viewModel.lastExpenseMonthIndex = 0
        viewModel.lastIncomeMonthIndex = 0


        val snackbar = Snackbar.make(binding.snackbarContainer, "Operation deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            deletedOperation.isForDelete = false
            viewModel.undoDelete(deletedOperation)
        }
            .setActionTextColor(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary,context?.getResources()!!.getColor(
                com.google.android.material.R.color.primary_material_light)))


        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                viewModel.total()
                binding.totalAmount.text = viewModel.totalSum.value.toString()
            }
        })

        viewModel.totalSum.observe(viewLifecycleOwner, Observer {
            binding.totalAmount.text = it.toString()
        })


        binding.operationsList.layoutManager = LinearLayoutManager(context)


        viewModel.operationsList.observe(viewLifecycleOwner, Observer {
            binding.operationsList.adapter = OperationsAdapter(it, findNavController(), viewModel)
        })

        val itemTouchHelper = ItemTouchHelper(
            CustomItemTouchHelperCallback(
                0,
                ItemTouchHelper.RIGHT,
                object : CustomItemTouchHelperCallback.OnSwipeListener {
                    override fun onSwipe(viewHolder: RecyclerView.ViewHolder) {
                        viewModel.deleteOperation(viewModel.operationsList.value!![viewHolder.adapterPosition])
                        deletedOperation = viewModel.operationsList.value!![viewHolder.adapterPosition]
                        snackbar.show()
                    }
                }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.operationsList)


        binding.addExpense.setOnClickListener{
            findNavController().navigate(R.id.action_operations_to_addExpenseFragment)
        }
        binding.addIncome.setOnClickListener {
            findNavController().navigate(R.id.action_operations_to_addIncomeFragment)
        }
        binding.addTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_operations_to_transferFragment)
        }

        binding.addOperation.setOnClickListener{
            if (isExpanded){
                shrinkFab()

            }
            else{
                expandFab()
            }
        }
        isExpanded = false




        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isExpanded){
                shrinkFab()
            }
            else {
                requireActivity().finish()
            }
        }

        binding.overlay.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (isExpanded) {
                    val fabConstraintRect = Rect()
                    binding.fabConstraint.getGlobalVisibleRect(fabConstraintRect)
                    if (!fabConstraintRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        shrinkFab()
                    }
                }
            }
            true
        }


        return root
    }



    private fun setButtonsClickable(isClickable: Boolean) {
        binding.addExpense.isClickable = isClickable
        binding.addIncome.isClickable = isClickable
        binding.addTransfer.isClickable = isClickable
    }


    private fun expandFab() {
        binding.addOperation.startAnimation(rotateOpen)
        binding.addExpense.startAnimation(fromBottom)
        binding.addIncome.startAnimation(fromBottom)
        binding.addTransfer.startAnimation(fromBottom)


        binding.bgTransfer.startAnimation(fromBottomBg)
        binding.bgExpense.startAnimation(fromBottomBg)
        binding.bgIncome.startAnimation(fromBottomBg)
        binding.expenseText.startAnimation(fromBottom)
        binding.incomeText.startAnimation(fromBottom)
        binding.transferText.startAnimation(fromBottom)
        setButtonsClickable(true)
        isExpanded = true
        binding.overlay.visibility = View.VISIBLE
    }

    private fun shrinkFab() {
        binding.addOperation.startAnimation(rotateClose)
        binding.addExpense.startAnimation(toBottom)
        binding.addIncome.startAnimation(toBottom)
        binding.addTransfer.startAnimation(toBottom)


        binding.bgTransfer.startAnimation(toBottom)
        binding.bgExpense.startAnimation(toBottom)
        binding.bgIncome.startAnimation(toBottom)
        binding.expenseText.startAnimation(toBottom)
        binding.incomeText.startAnimation(toBottom)
        binding.transferText.startAnimation(toBottom)
        setButtonsClickable(false)
        isExpanded = false
        binding.overlay.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}