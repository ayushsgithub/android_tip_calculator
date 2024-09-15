package com.example.tip

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_PEOPLE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvSplitLabel: TextView
    private lateinit var tvSplitBy: SeekBar
    private lateinit var tvPerPerson: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etBaseAmount = findViewById(R.id.etBaseAmout)
        seekBarTip = findViewById(R.id.seekBar)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvPercent = findViewById(R.id.tvPercent)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        tvSplitLabel = findViewById(R.id.tvSplitLabel)
        tvSplitBy = findViewById(R.id.tvSplitBy)
        tvPerPerson = findViewById(R.id.tvPerPerson)

        tvSplitLabel.text = "Split By 1"
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvSplitBy.progress = INITIAL_PEOPLE
        tvPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        computePerPerson(INITIAL_PEOPLE)
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.i(TAG, "onProgressChanged $progress")
                tvPercent.text = "$progress%"
                computeTipAndTotal()
                computePerPerson(tvSplitBy.progress)
                updateTipDescription(progress)

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        tvSplitBy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSplitLabel.text = "Split By $progress"
                computePerPerson(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
//                Log.i(TAG, "afterEnteringValue $s")
                computeTipAndTotal()
                computePerPerson(tvSplitBy.progress)
            }

        })
    }

    private fun computePerPerson(person: Int) {
        if (etBaseAmount.text.isEmpty() || tvTotalAmount.text.isEmpty() || person == 0) {
            tvPerPerson.text = ""
            return
        }

        val total = tvTotalAmount.text.toString().toDouble()
        val res = total / person

        tvPerPerson.text = "%.2f".format(res)
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription

        val color = android.animation.ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int


        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvPerPerson.text = ""
            return
        }
        val base = etBaseAmount.text.toString().toDouble()
        val tip = seekBarTip.progress

        val tipAmount = base * tip / 100

        val totalAmount = base + tipAmount

        tvTipAmount.text = "%.2f".format(tipAmount)

        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}