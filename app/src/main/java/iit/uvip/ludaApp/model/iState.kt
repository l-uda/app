package iit.uvip.ludaApp.model

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.View
import iit.uvip.ludaApp.R
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESET
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESTART
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_SUCCESS
import iit.uvip.ludaApp.view.MainFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.albaspazio.core.accessory.getArrayOrNull
import org.albaspazio.core.accessory.jsonObject
import org.albaspazio.core.ui.show1MethodDialog

// by default: (if I don't set mPressStatus), all btAction button are disabled
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
            fragment.stopPolling()
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
    }

    open fun setComponentsVisibility(data:String){

        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.txtGroup.visibility     = View.VISIBLE
        fragment.txtUDA.visibility       = View.VISIBLE

        fragment.spGroup.visibility      = View.INVISIBLE

        fragment.btAction2.visibility    = View.INVISIBLE
        fragment.btAbort.visibility      = View.VISIBLE
    }
}


// -1
class NotPolling(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_start), res.getString(R.string.action_start))

    override fun setButtonAction(){
        fragment.btAction.setOnClickListener{ fragment.startPolling() }

        fragment.btAbort.setOnClickListener{}
    }

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        fragment.txtStatus.visibility    = View.VISIBLE
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.btAbort.visibility      = View.INVISIBLE
    }
}

// 0
class NoSession(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_idle), res.getString(R.string.action_idle))

    override fun setButtonAction(){
        fragment.btAction.setOnClickListener{
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
            fragment.stopPolling()
        }

        fragment.btAbort.setOnClickListener{}
    }

    override fun setComponentsVisibility(data:String) {
        super.setComponentsVisibility(data)
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.btAbort.visibility      = View.INVISIBLE
    }
}

// 1
class WaitApp(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int = RemoteConnector.GROUP_SENT

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_wait_app), res.getString(R.string.action_wait_app))

    override fun setButtonAction(){
        fragment.btAction.setOnClickListener{
            fragment.insertGroupID(fragment.spGroup.selectedItemPosition+1)
        }

        fragment.btAbort.setOnClickListener{
            fragment.viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
            fragment.stopPolling()
        }
    }

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)
        
        fragment.txtGroup.visibility     = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE
        fragment.spGroup.visibility      = View.VISIBLE
    }
}

// 2... MainFragment.insertGroupID -> viewModel.setGroupID -> remoteConnector.setGroup   --------------|
//      MainFragment.status.observe <- StatusVM.status.value  <- newServerEvent.accept(group_sent)  <--|
class GroupSent(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String> = Pair(res.getString(R.string.status_group_sent), res.getString(R.string.action_group_sent))

    override fun apply(data:String) {
        super.apply(data)

        if(data == "-1")
            show1MethodDialog(fragment.requireActivity(), res.getString(R.string.warning), res.getString(R.string.group_wrong))
                            {   fragment.viewModel.status.value = Status(STATUS_SUCCESS, RemoteConnector.WAIT_APP, "")  }
        else
            fragment.mGroupId = data.toInt()
    }

    override fun setUITexts(data:String){
        super.setUITexts(data)
        fragment.txtGroup.text = if(data == "-1")   res.getString(R.string.group_wrong)
                                 else               data                        // resources.getString(R.string.group_defined, d.toString())
    }

    override fun setComponentsVisibility(data:String){
        super.setComponentsVisibility(data)

        fragment.txtStatus.visibility    = View.INVISIBLE
        fragment.txtUDA.visibility       = View.INVISIBLE        
    }
}

// 3
class ReachUda(frg:MainFragment, res:Resources):State(frg,res){

    override var mPressStatus:Int               = RemoteConnector.REACHING_UDA
    override var message:Pair<String, String>   = Pair(res.getString(R.string.status_reach_uda), res.getString(R.string.action_reach_uda))

    @SuppressLint("SetTextI18n")
    override fun setUITexts(data:String){
        fragment.txtStatus.text         = message.first + " " + data
        fragment.txtUDA.text            = data

        fragment.btAction.text          = message.second
        fragment.btAction.visibility    = View.VISIBLE
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
}

// 22
class ErrorServer(frg:MainFragment, res:Resources):State(frg,res){
    override var message:Pair<String, String> = Pair(res.getString(R.string.status_error_srv), res.getString(R.string.action_error_srv))
}

// 1003 RECEIVED_UDA_ID, used only to save uda_id when the app reconnect on an already open session
class ReConnected(frg:MainFragment, res:Resources):State(frg,res){

    override var message:Pair<String, String>   = Pair(res.getString(R.string.status_reconnected), res.getString(R.string.action_reconnected))

    override fun setUITexts(data:String){
        fragment.txtStatus.text         = message.first + " " + data
        fragment.txtUDA.text            = data

        fragment.btAction.visibility    = View.INVISIBLE
    }
}

