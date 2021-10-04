package iit.uvip.ludaApp.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import iit.uvip.ludaApp.R
import kotlinx.android.synthetic.main.fragment_button_answers.*
import org.albaspazio.core.accessory.getArrayOrNull
import org.albaspazio.core.accessory.jsonObject


class AnswerButtonsDF: DialogFragment() {

    open val LOG_TAG: String = AnswerButtonsDF::class.java.simpleName

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

              for (a in 0 until arr.length()) {
                val answ = arr.get(a) as String
                createButtonDynamically(layout, answ)
                answers.add(answ)
            }
        }
    }

    private fun createButtonDynamically(layout: LinearLayout, text:String) {
        // creating the button
        val dynamicButton = Button(context)
        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(250,100)

        dynamicButton.text = text
        dynamicButton.setBackgroundColor(Color.GREEN)
        dynamicButton.setOnClickListener {
            sendResult((it as Button).text as String)
        }
        // add Button to layout
        layout.addView(dynamicButton)
    }


    private fun sendResult(answer:String) {
        if (targetFragment == null) {
            return
        }
        val intent = Intent()
        intent.putExtra("answer", answer)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }
}
