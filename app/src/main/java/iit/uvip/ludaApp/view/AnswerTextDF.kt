package iit.uvip.ludaApp.view

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import iit.uvip.ludaApp.R
import kotlinx.android.synthetic.main.fragment_text_answers.*
import org.albaspazio.core.accessory.jsonObject
import org.albaspazio.core.ui.showToast

class AnswerTextDF: DialogFragment() {

//    val LOG_TAG: String = AnswerTextDF::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text_answers, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data:String = arguments?.getString("data", "") ?: ""

        val json            = data.jsonObject
        txtQuestion.text    = json?.getString("question")!!

        when(json.getString("input_type")){
            ""  ->   txtAnswer.inputType     = InputType.TYPE_CLASS_TEXT
            "0" ->   txtAnswer.inputType     = InputType.TYPE_CLASS_NUMBER
        }

        btSubmit.setOnClickListener {
            if(txtAnswer.text.isEmpty())
                            showToast("Inserisci la risposta", requireContext(), Toast.LENGTH_LONG)
            else            sendResult(txtAnswer.text.toString())
        }
    }

    private fun sendResult(answer:String) {
        requireActivity().supportFragmentManager.setFragmentResult(MainFragment.TARGET_FRAGMENT_ANSWER_REQUEST, bundleOf(Pair("answer", answer)))
        dismiss()
    }
}
