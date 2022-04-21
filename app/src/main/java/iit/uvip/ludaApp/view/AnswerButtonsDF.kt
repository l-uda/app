package iit.uvip.ludaApp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import iit.uvip.ludaApp.R
import kotlinx.android.synthetic.main.fragment_button_answers.*
import org.albaspazio.core.accessory.getArrayOrNull
import org.albaspazio.core.accessory.jsonObject

class AnswerButtonsDF: DialogFragment() {

//    val LOG_TAG: String = AnswerButtonsDF::class.java.simpleName

    private val answers = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_button_answers, container)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data:String = arguments?.getString("data", "") ?: ""

        val json            = data.jsonObject
        txtQuestion.text    = json?.getString("question") ?: ""

        val arr = json?.getArrayOrNull("input_type")
        if(arr != null) {
            for(a in 0 until arr.length()) {
                val answ = arr.get(a) as String
                createButtonDynamically(layout, answ)
                answers.add(answ)
            }
        }
        btPause.setOnClickListener {
            sendPause()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createButtonDynamically(layout: LinearLayout, text:String) {
        // creating the button
        val dynamicButton = Button(context)
        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(250,300)
        (dynamicButton.layoutParams as LinearLayout.LayoutParams).setMargins(20, 20, 20, 20);
        dynamicButton.setTextAppearance(requireActivity(), R.style.AnswerButton);
//        dynamicButton.setTextColor(Color.WHITE)
        dynamicButton.setBackgroundColor(resources.getColor(R.color.color1))
        dynamicButton.text = text

        dynamicButton.typeface = resources.getFont(R.font.rubik_medium);
        dynamicButton.setOnClickListener {
            sendResult((it as Button).text as String)
        }
        // add Button to layout
        layout.addView(dynamicButton)
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
