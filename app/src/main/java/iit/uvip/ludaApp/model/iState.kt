package iit.uvip.ludaApp.model

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import iit.uvip.ludaApp.R
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESET
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESTART
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_SUCCESS
import iit.uvip.ludaApp.view.MainFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.albaspazio.core.accessory.getArrayOrNull
import org.albaspazio.core.accessory.jsonObject
import org.albaspazio.core.ui.show1MethodDialog
import java.util.*

// by default:  btAbort visible and active (send 2 RESET)
//              (if I don't set mPressStatus), all btAction button are disabled
//              I set txtStatus.text
abstract class State(val fragment: MainFragment, val res:Resources) {

    protected open var mPressStatus:Int = MainFragment.NO_ACTION
    protected open lateinit var message:Pair<String,String>

    open fun apply(status:Status) {
        setUITexts(status)
        setButtonAction()
        setComponentsVisibility(status)
    }

    open fun setButtonAction(){
        if(mPressStatus == MainFragment.NO_ACTION)
        {
            fragment.btAction.setOnClickListener{}
            fragment.btAction.setOnLongClickListener{false}
        }
        else {
            fragment.btAction.setOnClickListener{ fragment.put(mPressStatus, "") }
            fragment.btAction.setOnLongClickListener{ false }
        }

        fragment.btAction2.setOnClickListener {}
        fragment.btAbort.setOnClickListener {}
        fragment.btAbort.setOnLongClickListener {
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET)
            false
        }
    }

    open fun setUITexts(status:Status){
        fragment.txtStatus.text = message.first

        if(message.second.isEmpty())
            fragment.btAction.visibility = View.INVISIBLE
        else {
            fragment.btAction.text       = message.second
            fragment.btAction.visibility = View.VISIBLE
        }
    }

    open fun setComponentsVisibility(status:Status){

        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.btAbort.visibility      = View.VISIBLE

        fragment.spGroup.visibility      = View.INVISIBLE
        fragment.spExplorer.visibility   = View.INVISIBLE

        fragment.btAction.visibility     =  if(mPressStatus == MainFragment.NO_ACTION)      View.INVISIBLE
                                            else                                            View.VISIBLE
        fragment.btAction2.visibility    = View.INVISIBLE
    }
}

// -1: RESET:  starting state. polling not active
// abort invisible
class NotPolling(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_start), res.getString(R.string.action_start))

    override fun apply(status:Status) {
        super.apply(status)
        fragment.stopPolling()
        fragment.stopBlinking(false)
        fragment.resetUdas()
    }

    override fun setUITexts(status:Status){
        super.setUITexts(status)

        val intent = Intent("GROUP_UPDATE")
        intent.putExtra("data", res.getString(R.string.group_unregistered))
        LocalBroadcastManager.getInstance(fragment.requireContext()).sendBroadcast(intent)
    }

    override fun setButtonAction(){
        super.setButtonAction()
        fragment.btAction.setOnClickListener{ fragment.startPolling() }
        fragment.btAction.setOnLongClickListener{ false }
        fragment.btAbort.setOnClickListener{}
        fragment.btAbort.setOnLongClickListener{false}
    }

    override fun setComponentsVisibility(status:Status){
        super.setComponentsVisibility(status)

        fragment.btAction.visibility     = View.VISIBLE
        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.btAbort.visibility      = View.INVISIBLE
        fragment.setUDASubject("", false)
    }
}

// 0:  user pressed connect, but UDA was not initialized
class NoSession(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_idle), res.getString(R.string.action_idle))

    override fun setUITexts(status:Status){
        super.setUITexts(status)

        val intent = Intent("GROUP_UPDATE")
        intent.putExtra("data", res.getString(R.string.group_unregistered))
        LocalBroadcastManager.getInstance(fragment.requireContext()).sendBroadcast(intent)
    }

    override fun setButtonAction(){
        super.setButtonAction()

        // do the same things as btAbort
        fragment.btAction.setOnClickListener{
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET)
        }
        fragment.btAction.setOnLongClickListener{
false        }

        fragment.btAbort.setOnClickListener{}
        fragment.btAbort.setOnLongClickListener{            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, listOf())
            true}
    }

    override fun setComponentsVisibility(status:Status) {
        super.setComponentsVisibility(status)
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.setUDASubject("", false)
    }
}

// 1: user asked to connect and a session was present
// the app receives Subject information. user must register the app
// with(out) the explorer combo
class WaitApp(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int = RemoteConnector.GROUP_SENT

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_wait_app), res.getString(R.string.action_wait_app))

    override fun setButtonAction(){
        super.setButtonAction()
        fragment.btAction.setOnClickListener{
            fragment.insertGroupID()
        }
        fragment.btAction.setOnLongClickListener{
false
        }
        fragment.btAbort.setOnClickListener{}
        fragment.btAbort.setOnLongClickListener{
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, listOf())
            true
        }
    }

    // this data contains a json with the Subject description
    // e.g. : "{\"id\":\"3\",\"nome\":\"Storia\",\"posizione\":\"1\",\"descrizione\":\"La materia Storia blah, blah, blah\", \"has_subgroups\":0 or 1}"
    override fun setComponentsVisibility(status:Status){
        super.setComponentsVisibility(status)

        val uda_subject    = status.data.jsonObject
        val uda_name       = uda_subject?.getString("nome")?.toUpperCase(Locale.ROOT) ?: ""
        val has_subgroups  = uda_subject?.getInt("has_subgroups") ?: 0
        val color          = Color.parseColor(uda_subject?.getString("color") ?: "#000000")
        val group_id       = status.hint?.getOrNull(0) ?: 1
        var explorer_id    = status.hint?.getOrNull(1) ?: 1

        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE

        fragment.spGroup.visibility      = View.VISIBLE
        fragment.spExplorer.visibility   = if(has_subgroups>0)   View.VISIBLE
                                           else                  View.INVISIBLE


        fragment.setUDASubject(uda_name, has_subgroups>0, color)
        // Prefill group id and explorer id, if one is suggested
        fragment.setUDAExplorer(group_id, explorer_id)
    }
}

// 2... MainFragment.insertGroupID -> viewModel.setGroupID -> remoteConnector.setGroup   --------------|
//      MainFragment.status.observe <- StatusVM.status.value  <- newServerEvent.accept(group_sent)  <--|
class GroupSent(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_group_sent), res.getString(R.string.action_group_sent))

    // if UDA was not running (server in WAIT_APP status) => data is uda_id_by_app
    override fun apply(status:Status) {
//        super.apply(status)

        if(status.data == "-1")
            show1MethodDialog(fragment.requireActivity(), res.getString(R.string.warning), res.getString(R.string.group_wrong))
                            {   fragment.viewModel.status.value = Status(STATUS_SUCCESS, RemoteConnector.WAIT_APP)  }
        else {
            val udas:List<Int>      = status.uda_id
            fragment.setUdaCompletion(udas)
            fragment.groupConfirmed(status.data)
        }
    }

    override fun setComponentsVisibility(status:Status){
        super.setComponentsVisibility(status)

        fragment.txtStatus.visibility    = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE        
    }
}

// 3
class ReachUda(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int               = RemoteConnector.READY
    override var message:Pair<String, String>   = Pair(res.getString(R.string.status_reach_uda), res.getString(R.string.action_reach_uda))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(status:Status){
        super.setUITexts(status)

        fragment.txtStatus.text         = message.first
//        fragment.txtUDA.text            = status.uda_id.toString()

        fragment.btAction.text          = message.second
        fragment.btAction.visibility    = View.VISIBLE
    }

    // data is udaid_to_reach
    override fun setComponentsVisibility(status:Status) {
        super.setComponentsVisibility(status)

        val uda2reach = status.uda_id[status.uda_id.size-1]

        fragment.blinkUDA2Reach(uda2reach.toString())
    }
}

// 4
//class ReachingUda(frg:MainFragment, res:Resources):State(frg,res){
//    override var message:Pair<String, String> = Pair(res.getString(R.string.status_reaching_uda), res.getString(R.string.action_reaching_uda))
//
//    override var mPressStatus:Int = RemoteConnector.READY
//
//    // data is udaid_to_reach
//    override fun setComponentsVisibility(status:Status) {
//        super.setComponentsVisibility(status)
//
//        val uda2reach = status.uda_id[status.uda_id.size-1]
//
//        fragment.blinkUDA2Reach(uda2reach.toString())
//    }
//}

// 5
class Ready(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_ready), res.getString(R.string.action_ready))

    override var mPressStatus:Int = RemoteConnector.REACH_UDA
    override fun setButtonAction(){
        super.setButtonAction()
        fragment.btAction.setOnClickListener{ }
        fragment.btAction.setOnLongClickListener{false }
        fragment.btAction.setOnLongClickListener{
            fragment.put(mPressStatus, "")
            true
        }
    }
    override fun apply(status:Status) {
        super.apply(status)
        fragment.stopBlinking()
    }
}

// 7
class Started(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair("","")  //Pair(res.getString(R.string.status_started), res.getString(R.string.action_started))

    override var mPressStatus:Int = MainFragment.NO_ACTION //RemoteConnector.PAUSE
}

// 8
class Pause(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_pause), res.getString(R.string.action_pause))
}

// 9
class Paused(frg:MainFragment, res:Resources):State(frg,res){
//    override var message:Pair<String, String> = Pair(res.getString(R.string.status_paused), res.getString(R.string.action_paused))
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_paused), "")

    override var mPressStatus:Int = MainFragment.NO_ACTION //RemoteConnector.RESUME

    override fun setComponentsVisibility(status:Status){
        super.setComponentsVisibility(status)

        fragment.btAction2.visibility   = View.INVISIBLE
        fragment.btAction2.text         = res.getString(R.string.text_ricomincia)
    }

    override fun setButtonAction(){
        super.setButtonAction()
        fragment.btAction2.setOnClickListener{ fragment.put(RESTART, "") }
    }
}

// 10
class Resume(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_resume), res.getString(R.string.action_resume))
}

// 11
class Abort(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_abort), res.getString(R.string.action_abort))
}

// 12
class Aborted(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_aborted), res.getString(R.string.action_aborted))
}

// 13
class Restart(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_restart), res.getString(R.string.action_restart))
}

//14
/*
put(uda_id, WAIT_DATA, data)
data = {"question": " testo domanda", "input_type": v}

con v :
""                  = tastiera alfanumerica
0                   = tastierino solo numerico
["t1","t2","t3"]    = N bottoni con testo indicato in t1, t2, t3

la uda poi riceve nel suo get : DATA_SENT, data
con data= "il testo inserito | il numero immesso | il testo del pulsante premuto"

 */
class WaitData(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair("","")   //(res.getString(R.string.status_wait_data), res.getString(R.string.action_wait_data))

    override var mPressStatus:Int = RemoteConnector.DATA_SENT

    override fun setComponentsVisibility(status:Status){
        super.setComponentsVisibility(status)

        val json        = status.data.jsonObject
        val question    = json?.getString("question") ?: ""

        if (question.isEmpty())
            throw MyException(MainFragment.ERROR_QUESTION_EMPTY, "QUESTION EMPTY")

        val type:Int = when(json?.getString("input_type")){
            ""      -> MainFragment.QUESTION_TYPE_STR
            "NO_AUTOSUGGEST"      -> MainFragment.QUESTION_TYPE_STR
            "0"     -> MainFragment.QUESTION_TYPE_NUM
            else    -> {
                val arr = json?.getArrayOrNull("input_type")
                if (arr != null) {
                    if (arr.length() > 0) MainFragment.QUESTION_TYPE_ARR
                    else                    throw MyException(MainFragment.ERROR_ANSWERS_EMPTY, "no answers specified")
                } else throw MyException(MainFragment.ERROR_ANSWERS_EMPTY, "error in QA format")
            }
        }
        fragment.showAnswerDialog(status.data, type)
    }
}

// 15
class DataSent(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_data_sent), res.getString(R.string.action_data_sent))
}

// 16
class Completed(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_completed), res.getString(R.string.action_completed))

    override fun apply(status:Status) {
        super.apply(status)
        if(status.uda_id.isEmpty())
            return
        fragment.udaCompleted(status.uda_id[status.uda_id.size-1])
    }
}

// 18
class Finalized(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_finalized), res.getString(R.string.action_finalized))
}

// 19
class WaitServer(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_wait_server), res.getString(R.string.action_wait_server))
}

// 20
class ErrorUDA(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_error_uda), res.getString(R.string.action_error_uda))
}

// 21
class ErrorApp(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_error_app), res.getString(R.string.action_error_app))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(status: Status) {
        super.setUITexts(status)
        fragment.txtStatus.text = "${fragment.txtStatus.text}\n$status"
    }
}

// 22
class ErrorServer(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_error_srv), res.getString(R.string.action_error_srv))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(status: Status) {
        super.setUITexts(status)
        fragment.txtStatus.text = "${fragment.txtStatus.text}\n${status.data}"
    }
}
