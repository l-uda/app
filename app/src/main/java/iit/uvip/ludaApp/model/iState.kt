package iit.uvip.ludaApp.model

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
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

// by default:  btAbort visible and active (send 2 RESET)
//              (if I don't set mPressStatus), all btAction button are disabled
//              I set txtStatus.text
abstract class State(val fragment: MainFragment, val res:Resources) {

    protected open var mPressStatus:Int = MainFragment.NO_ACTION
    protected open lateinit var message:Pair<String,String>

    open fun apply(data:String="") {
        setUITexts(data)
        setButtonAction()
        setComponentsVisibility(data)
    }

    open fun setButtonAction(){
        if(mPressStatus == MainFragment.NO_ACTION)  fragment.btAction.setOnClickListener{}
        else                                        fragment.btAction.setOnClickListener{ fragment.put(mPressStatus, "") }

        fragment.btAction2.setOnClickListener{}

        fragment.btAbort.setOnClickListener{
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
//            fragment.stopPolling()
        }
    }

    open fun setUITexts(data:String=""){
        fragment.txtStatus.text = message.first

        if(message.second.isEmpty())
            fragment.btAction.visibility = View.INVISIBLE
        else {
            fragment.btAction.text       = message.second
            fragment.btAction.visibility = View.VISIBLE
        }

        fragment.txtUrl.visibility       = View.INVISIBLE
    }

    open fun setComponentsVisibility(data:String){

        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.btAbort.visibility      = View.VISIBLE
//        fragment.txtGroup.visibility     = View.VISIBLE
//        fragment.txtUDA.visibility       = View.VISIBLE


//        fragment.ivTargetUDA.visibility  = View.INVISIBLE
        fragment.spGroup.visibility      = View.INVISIBLE
        fragment.btAction2.visibility    = View.INVISIBLE
    }
}


// -1: RESET:  abort invisible
class NotPolling(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_start), res.getString(R.string.action_start))

    override fun apply(data:String) {
        super.apply(data)
        fragment.stopPolling()
        fragment.stopBlinking(false)
    }


    override fun setUITexts(data:String){
        super.setUITexts(data)

        val intent = Intent("GROUP_UPDATE")
        intent.putExtra("data", "NON REGISTRATO")
        LocalBroadcastManager.getInstance(fragment.context!!).sendBroadcast(intent)

        fragment.txtGroup.text = "NON REGISTRATO"
    }


    override fun setButtonAction(){
        super.setButtonAction()
        fragment.btAction.setOnClickListener{ fragment.startPolling() }

        fragment.btAbort.setOnClickListener{}
//        fragment.btAbort.setOnClickListener{
//            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
////            fragment.stopPolling()
//        }
    }

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.btAbort.visibility      = View.INVISIBLE
//        fragment.txtUrl.visibility       = View.VISIBLE
        fragment.setUDASubject("")
    }
}

// 0
class NoSession(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_idle), res.getString(R.string.action_idle))

    override fun setUITexts(data:String){
        super.setUITexts(data)

        val intent = Intent("GROUP_UPDATE")
        intent.putExtra("data", "NON REGISTRATO")
        LocalBroadcastManager.getInstance(fragment.context!!).sendBroadcast(intent)

        fragment.txtGroup.text = "NON REGISTRATO"
    }

    override fun setButtonAction(){
        super.setButtonAction()

        // do the same things as btAbort
        fragment.btAction.setOnClickListener{
//            fragment.stopPolling()
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
        }

        fragment.btAbort.setOnClickListener{}
    }

    override fun setComponentsVisibility(data:String) {
        super.setComponentsVisibility(data)
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.btAbort.visibility      = View.INVISIBLE
        fragment.setUDASubject("")
    }
}

// 1
class WaitApp(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int = RemoteConnector.GROUP_SENT

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_wait_app), res.getString(R.string.action_wait_app))

    override fun setButtonAction(){
        super.setButtonAction()

        fragment.btAction.setOnClickListener{
            fragment.insertGroupID(fragment.spGroup.selectedItemPosition+1)
        }

//        fragment.btAbort.setOnClickListener{
//            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
////            fragment.stopPolling()
//        }
    }

    // this data contains a json with the Subject description
    // e.g. : "{\"id\":\"3\",\"nome\":\"Storia\",\"posizione\":\"1\",\"descrizione\":\"La materia Storia blah, blah, blah\"}"
    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        val uda_subject    = data.jsonObject
        val uda_name       = uda_subject?.getString("nome")?.toUpperCase() ?: ""

        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.spGroup.visibility      = View.VISIBLE

        fragment.setUDASubject(uda_name)
    }
}

// 2... MainFragment.insertGroupID -> viewModel.setGroupID -> remoteConnector.setGroup   --------------|
//      MainFragment.status.observe <- StatusVM.status.value  <- newServerEvent.accept(group_sent)  <--|
class GroupSent(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_group_sent), res.getString(R.string.action_group_sent))

    // if UDA was not running (server in WAIT_APP status) => data is uda_id_by_app
    override fun apply(data:String) {
        super.apply(data)

        if(data == "-1")
            show1MethodDialog(fragment.requireActivity(), res.getString(R.string.warning), res.getString(R.string.group_wrong))
                            {   fragment.viewModel.status.value = Status(STATUS_SUCCESS, RemoteConnector.WAIT_APP, "")  }
        else
            fragment.groupConfirmed(data)

    }

//    override fun setUITexts(data:String){
//        super.setUITexts(data)
//        if(data == "-1") return
//
//
//        val group = if(data == "-1")   res.getString(R.string.group_wrong)
//                    else               data                        // resources.getString(R.string.group_defined, d.toString())
//
//        val intent = Intent("GROUP_UPDATE")
//        intent.putExtra("data", "REGISTRATO COME GRUPPO $group")
//        LocalBroadcastManager.getInstance(fragment.context!!).sendBroadcast(intent)
//
//        fragment.txtGroup.text = group
//    }

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        fragment.txtStatus.visibility    = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE        
    }
}


// 103 RECEIVED_UDA_ID, used only to save uda_id when the app reconnect on an already open session
//class ReConnected(frg:MainFragment, res:Resources):State(frg,res){
//
//    override var message:Pair<String, String>   = Pair(res.getString(R.string.status_reconnected), res.getString(R.string.action_reconnected))
//
//    @SuppressLint("SetTextI18n")
//    override fun setUITexts(data:String){
//        super.setUITexts(data)
//        fragment.txtStatus.text         = "${message.first} $data"
//        fragment.txtUDA.text            = data
//
//        fragment.btAction.visibility    = View.INVISIBLE
//    }
//}




// 3
class ReachUda(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int               = RemoteConnector.REACHING_UDA
    override var message:Pair<String, String>   = Pair(res.getString(R.string.status_reach_uda), res.getString(R.string.action_reach_uda))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(data:String){
        super.setUITexts(data)

        fragment.txtStatus.text         = message.first
        fragment.txtUDA.text            = data

        fragment.btAction.text          = message.second
        fragment.btAction.visibility    = View.VISIBLE
    }

    // data is udaid_to_reach
    override fun setComponentsVisibility(data: String) {
        super.setComponentsVisibility(data)
        fragment.blinkUDA2Reach(data)
    }
}

// 4
class ReachingUda(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_reaching_uda), res.getString(R.string.action_reaching_uda))

    override var mPressStatus:Int = RemoteConnector.READY
}

// 5
class Ready(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_ready), res.getString(R.string.action_ready))

    override var mPressStatus:Int = RemoteConnector.REACHING_UDA

    override fun apply(data:String) {
        super.apply(data)
        fragment.stopBlinking()
    }
}

// 7
class Started(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_started), res.getString(R.string.action_started))

    override var mPressStatus:Int = RemoteConnector.PAUSE
}

// 8
class Pause(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_pause), res.getString(R.string.action_pause))
}

// 9
class Paused(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_paused), res.getString(R.string.action_paused))

    override var mPressStatus:Int = RemoteConnector.RESUME

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        fragment.btAction2.visibility   = View.VISIBLE
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
data = {"answer": " testo domanda", "input_type": v}

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

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        val json        = data.jsonObject
        val question    = json?.getString("question") ?: ""

        if (question.isEmpty())
            throw MyException(MainFragment.ERROR_QUESTION_EMPTY, "QUESTION EMPTY")

        val input_type  = json?.getString("input_type")

        val type:Int = when(input_type){
            ""      -> MainFragment.QUESTION_TYPE_STR
            "0"     -> MainFragment.QUESTION_TYPE_NUM

            else    -> {
                val arr = json?.getArrayOrNull("input_type")
                if (arr != null) {
                    if (arr.length() > 0) MainFragment.QUESTION_TYPE_ARR
                    else                    throw MyException(MainFragment.ERROR_ANSWERS_EMPTY, "no answers specified")
                } else throw MyException(MainFragment.ERROR_ANSWERS_EMPTY, "error in QA format")
            }
        }
        fragment.showAnswerDialog(data, type)
    }
}

// 15
class DataSent(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_data_sent), res.getString(R.string.action_data_sent))
}

// 16
class Completed(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_completed), res.getString(R.string.action_completed))


    override fun apply(data:String) {
        super.apply(data)
        fragment.udaCompleted()
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
    override fun setUITexts(data: String) {
        super.setUITexts(data)
        fragment.txtStatus.text = "${fragment.txtStatus.text}\n$data"
    }
}

// 22
class ErrorServer(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_error_srv), res.getString(R.string.action_error_srv))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(data: String) {
        super.setUITexts(data)
        fragment.txtStatus.text = "${fragment.txtStatus.text}\n$data"
    }
}
