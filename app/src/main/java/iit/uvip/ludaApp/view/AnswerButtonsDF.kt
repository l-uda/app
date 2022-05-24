package iit.uvip.ludaApp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import iit.uvip.ludaApp.R
import kotlinx.android.synthetic.main.fragment_button_answers.*
import org.albaspazio.core.accessory.getArrayOrNull
import org.albaspazio.core.accessory.jsonObject
import kotlin.math.ceil


class AnswerButtonsDF: DialogFragment() {

//    val LOG_TAG: String = AnswerButtonsDF::class.java.simpleName

    private val answers = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_button_answers, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data:String = arguments?.getString("data", "") ?: ""

        val json            = data.jsonObject
        txtQuestion.text    = json?.getString("question") ?: ""

        val arr = json?.getArrayOrNull("input_type")
        if(arr != null) {

            //https://stackoverflow.com/questions/1528988/create-tablelayout-programmatically
            val tableParams = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT)
            tableParams.topMargin = 10
            tableParams.bottomMargin = 10

            val rowParams   = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
            rowParams.leftMargin = 10
            rowParams.rightMargin = 10

            val nbut    = arr.length()*1.0
            val maxcol  = 6

            val table           = TableLayout(requireContext())
            if(nbut > maxcol){
                val nrows       = ceil((nbut/maxcol).toDouble())
                val elem_x_row  = ceil(nbut/nrows).toInt()
                var cnt         = 0
                var row:TableRow? = null //         = TableRow(requireContext())


                for (a in 0 until arr.length()) {
                    if (cnt == 0){
                        row                 = TableRow(requireContext())
                        row.layoutParams    = tableParams;// TableLayout is the parent view
                    }

                    val answ        = arr.get(a) as String
                    val bt          = createButtonDynamically(answ)
                    bt.layoutParams = rowParams
                    row!!.addView(bt)
                    cnt++
                    if(cnt == elem_x_row){
                        cnt = 0
                        table.addView(row)
                    }
                }
                layout.addView(table)
            }
            else {
                for (a in 0 until arr.length()) {
                    val answ    = arr.get(a) as String
                    val bt      = createButtonDynamically(answ)
                    layout.addView(bt)
                    answers.add(answ)
                }
            }
        }
        btPause.visibility = View.INVISIBLE
        btPause.setOnClickListener {
            sendPause()
        }
    }

    private fun createButtonDynamically(text:String):Button {
        // creating the button
        val dynamicButton = Button(context)
        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(250,300)
        (dynamicButton.layoutParams as LinearLayout.LayoutParams).setMargins(20, 20, 20, 20)
        dynamicButton.setTextAppearance(requireActivity(), R.style.AnswerButton)
//        dynamicButton.setTextColor(Color.WHITE)
        dynamicButton.setBackgroundColor(resources.getColor(R.color.color1))
        dynamicButton.text = text

        dynamicButton.typeface = resources.getFont(R.font.rubik_medium)
        dynamicButton.setOnClickListener {
            sendResult((it as Button).text as String)
        }
        return dynamicButton
    }

    private fun sendResult(answer:String) {
        requireActivity().supportFragmentManager.setFragmentResult(MainFragment.TARGET_FRAGMENT_ANSWER_REQUEST, bundleOf(Pair("answer", answer)))
        dismiss()
    }

    private fun sendPause() {
        requireActivity().supportFragmentManager.setFragmentResult(MainFragment.TARGET_FRAGMENT_PAUSE_REQUEST, Bundle())
        dismiss()
    }
}
