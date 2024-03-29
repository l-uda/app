package iit.uvip.ludaApp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels

import iit.uvip.ludaApp.R
import iit.uvip.ludaApp.model.*
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ABORT
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ABORTED
import iit.uvip.ludaApp.viewmodel.StatusVM
import kotlinx.android.synthetic.main.fragment_main.*
import org.albaspazio.core.accessory.*
import org.albaspazio.core.fragments.BaseFragment
import org.albaspazio.core.ui.showAlert

import iit.uvip.ludaApp.model.RemoteConnector.Companion.IDLE
import iit.uvip.ludaApp.model.RemoteConnector.Companion.WAIT_APP
import iit.uvip.ludaApp.model.RemoteConnector.Companion.GROUP_SENT
import iit.uvip.ludaApp.model.RemoteConnector.Companion.REACH_UDA
import iit.uvip.ludaApp.model.RemoteConnector.Companion.REACHING_UDA
import iit.uvip.ludaApp.model.RemoteConnector.Companion.READY
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STARTED
import iit.uvip.ludaApp.model.RemoteConnector.Companion.PAUSE
import iit.uvip.ludaApp.model.RemoteConnector.Companion.PAUSED
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESUME
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESTART
import iit.uvip.ludaApp.model.RemoteConnector.Companion.WAIT_DATA
import iit.uvip.ludaApp.model.RemoteConnector.Companion.DATA_SENT
import iit.uvip.ludaApp.model.RemoteConnector.Companion.COMPLETED
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ERROR_APP
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ERROR_SERVER
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ERROR_UDA
import iit.uvip.ludaApp.model.RemoteConnector.Companion.FINALIZED
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESET
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_ERROR
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_SUCCESS
import iit.uvip.ludaApp.model.RemoteConnector.Companion.WAIT_SERVER

/*
- The App can only put the following status:

GROUP_SENT
REACHING_UDA
READY
PAUSE
RESUME
RESTART
DATA_SENT

it changes its own GUI according to the following:

RESET (returned by server when no session in present)
IDLE
WAIT_APP
GROUP_SENT (directly after a successfull put, not read by the following get)
STARTED
PAUSED
ABORTED
WAIT_DATA
COMPLETED
FINALIZED
*/

class MainFragment : BaseFragment(
    layout = R.layout.fragment_main,
    landscape = false,
    hideAndroidControls = false
){

    private val URL = "https://www.sagosoft.it/_API_/cpim/luda/www/luda_20210111_1500/api/app/"

    companion object {
        const val NO_STATUS = -2
        const val NO_ACTION = -2

        @JvmStatic val TARGET_FRAGMENT_ANSWER_REQUEST_CODE: Int    = 1

        const val ERROR_QUESTION_EMPTY      = 100
        const val ERROR_ANSWERS_EMPTY       = 101
        const val ERROR_UNRECOGNIZED_STATUS = 102
        const val ERROR_APP_NOT_ASSOCIATED  = 103

        const val QUESTION_TYPE_STR = 0
        const val QUESTION_TYPE_NUM = 1
        const val QUESTION_TYPE_ARR = 2
    }

    override val LOG_TAG = MainFragment::class.java.simpleName

    val viewModel:StatusVM by activityViewModels { viewModelFactory }
    private val viewModelFactory    = StatusVM.Factory(this, null, DependenciesProviderParam.getInstance(URL).remoteConnector)
    private var mStatus:Int         = NO_STATUS

    private lateinit var mState:State   // onViewCreated set RESET

    var mGroupId:Int = -1

    private var mIsOnline:Boolean   = false

    // unmanaged: START=6, FINALIZE=17, WAIT_SERVER=19
    private val states:HashMap<Int, State> by lazy { hashMapOf(
        RESET           to NotPolling(this, resources), // -1
        IDLE            to NoSession(this, resources),  // 0
        WAIT_APP        to WaitApp(this, resources),    // 1
        GROUP_SENT      to GroupSent(this, resources),  // 2
        REACH_UDA       to ReachUda(this, resources),   // 3
        REACHING_UDA    to ReachingUda(this, resources),// 4
        READY           to Ready(this, resources),      // 5
        STARTED         to Started(this, resources),    // 7
        PAUSE           to Pause(this, resources),      // 8
        PAUSED          to Paused(this, resources),     // 9
        RESUME          to Resume(this, resources),     // 10
        ABORT           to Abort(this, resources),      // 11
        ABORTED         to Aborted(this, resources),    // 12
        RESTART         to Restart(this, resources),    // 13
        WAIT_DATA       to WaitData(this, resources),   // 14
        DATA_SENT       to DataSent(this, resources),   // 15
        COMPLETED       to Completed(this, resources),  // 16
        FINALIZED       to Finalized(this, resources),  // 18
        WAIT_SERVER     to WaitServer(this, resources), // 19
        ERROR_UDA       to ErrorUDA(this, resources),   // 20
        ERROR_APP       to ErrorApp(this, resources),   // 21
        ERROR_SERVER    to ErrorServer(this, resources),// 22
    )}

    private fun setupObserver(){

        viewModel.status.observe(viewLifecycleOwner) {
//            Log.d("MAIN", "status: $it.status")

            var data = ""
            when(it.result) {
                STATUS_SUCCESS -> {
                    try{
                        mStatus = it.status
                        data    = it.data ?: ""
                        Log.d("Main", "new status $mStatus")
                    }
                    catch (e:Exception){
                        mStatus = ERROR_APP
                    }

                }
                STATUS_ERROR -> {
                    when(it.status){
                        ERROR_APP_NOT_ASSOCIATED -> {  // devo ignorare quanto arriva dal server perchè è sicuramente sbagliato
                            stopPolling()
                            mStatus = ERROR_APP
                        }
                        else -> mStatus = ERROR_SERVER
                    }
                }
            }
            try{
                mState = states[mStatus] ?: states[ERROR_SERVER]!!
                mState.apply(data)
            }
            catch (e:Exception){
                mStatus = ERROR_APP
                mState = states[mStatus] ?: states[ERROR_SERVER]!!
                mState.apply(data)
            }
        }
    }

    //region LIFECYCLE
    //======================================================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupsSpinner()
        setupObserver()
        viewModel.status.value = Status(STATUS_SUCCESS, RESET, "")
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        checkConnection()
    }
    //endregion======================================================================

    //region REMOTE CALLS
    //======================================================================
    fun put(status:Int, data:String = ""){
        if(checkConnection())
            if(status != NO_STATUS)
                viewModel.put(mGroupId, status, data)
    }

    fun insertGroupID(grp_id:Int){
        if(grp_id == -1){
            return
        }
        if(checkConnection())        viewModel.setGroupID(grp_id)
    }

    fun startPolling(){
        if(checkConnection())        viewModel.startPolling()
    }

    fun stopPolling(){
        mGroupId = -1
        if(checkConnection())        viewModel.stopPolling()
    }
    //endregion======================================================================

    //region ANSWERS
    //======================================================================
    fun showAnswerDialog(jsondata:String, type:Int){

        val bundle = Bundle()
        bundle.putString("data", jsondata)

        val df = if(type == QUESTION_TYPE_ARR)   AnswerButtonsDF()
                 else                            AnswerTextDF()

        df.arguments    = bundle
        df.setTargetFragment(this, TARGET_FRAGMENT_ANSWER_REQUEST_CODE)
        df.isCancelable = false
        df.show(parentFragmentManager, "Inserisci Risposta")
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        when(requestCode){
            TARGET_FRAGMENT_ANSWER_REQUEST_CODE -> {
                val answer = data?.getStringExtra("answer") ?: ""
                put(DATA_SENT, answer)

            }
        }
    }
    //endregion======================================================================

    //region ACCESSORY
    //======================================================================
    private fun initGroupsSpinner(){
        ArrayAdapter.createFromResource(requireContext(), R.array.groups_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spGroup.adapter = adapter
            }
    }

    private fun checkConnection():Boolean{

        mIsOnline = isOnline(requireContext())
        if(!mIsOnline){
            showAlert(requireActivity(), "Errore", "Connessione internet non disponibile")
            return false
        }
        return true
    }
    //endregion======================================================================
}