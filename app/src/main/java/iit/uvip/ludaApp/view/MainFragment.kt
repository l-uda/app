package iit.uvip.ludaApp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager

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
import android.graphics.drawable.Drawable
import android.widget.ImageView
import iit.uvip.ludaApp.BuildConfig.server_url
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.albaspazio.core.ui.loadDrawableFromName
import java.util.concurrent.TimeUnit


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

    private val URL = server_url //"https://www.sagosoft.it/_API_/cpim/luda/www/luda_20210111_1500/api/app/"

    companion object {
        const val NO_STATUS = -2
        const val NO_ACTION = -2

        @JvmStatic val TARGET_FRAGMENT_ANSWER_REQUEST: String = "TARGET_FRAGMENT_ANSWER_REQUEST"
        @JvmStatic val TARGET_FRAGMENT_PAUSE_REQUEST: String = "TARGET_FRAGMENT_PAUSE_REQUEST"

        const val ERROR_QUESTION_EMPTY      = 1000
        const val ERROR_ANSWERS_EMPTY       = 1001
        const val ERROR_UNRECOGNIZED_STATUS = 1002
        const val ERROR_APP_NOT_ASSOCIATED  = 1003

        const val QUESTION_TYPE_STR = 0
        const val QUESTION_TYPE_NUM = 1
        const val QUESTION_TYPE_ARR = 2
    }

    override val LOG_TAG = MainFragment::class.java.simpleName

    val viewModel:StatusVM by activityViewModels { viewModelFactory }

    private var compDispTimer: CompositeDisposable = CompositeDisposable()
    private var blinkFlag:Int = View.INVISIBLE

    private val remoteConnector: RemoteConnector = RemoteConnector()

//    private val viewModelFactory    = StatusVM.Factory(this, null, DependenciesProviderParam.getInstance(URL).remoteConnector)
    private val viewModelFactory    = StatusVM.Factory(this, null, remoteConnector)
    private var mStatus:Int         = NO_STATUS
    private var mCurrUdaId:String   = ""
    private var mSubjectName:String = ""

    private var isPolling:Boolean   = false     // this var is needed because stop polling takes time.
                                                // when I call a stop polling and go to -1, there can be a status 0 that take me back to 0 (Nosession)
                                                // with this flag I can ignore it
    private lateinit var mState:State   // onViewCreated set it to RESET

    var completedUDAsViews:List<ImageView> = listOf()

    var mGroupId:Int    = -1
    var mExplorerId:Int = -1

    private var mAnswerDF: DialogFragment?  = null
    private var mIsOnline:Boolean           = false

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
        ERROR_SERVER    to ErrorServer(this, resources)// 22
//        RECEIVED_UDA_ID to ReConnected(this, resources),// 1003
    )}

    private fun setupObserver(){

        viewModel.status.observe(viewLifecycleOwner) {
//            Log.d("MAIN", "status: $it.status")

            if(!isPolling) return@observe

//            var data = it.data ?: ""
            when(it.result) {
                STATUS_SUCCESS -> {
                    try{
                        mStatus = it.status
                        Log.d("Main", "new status $mStatus")
                    }
                    catch (e:Exception){
                        mStatus = ERROR_APP
                    }
                }
                STATUS_ERROR -> {
                    mStatus = when(it.status){
                        ERROR_APP_NOT_ASSOCIATED    -> ERROR_APP   // devo ignorare quanto arriva dal server perchè è sicuramente sbagliato
                        else                        -> ERROR_SERVER
                    }
//                    stopPolling()
                }
            }
            try{
                // CHANGE STATE !!
                mAnswerDF?.dismiss()
                mAnswerDF = null
                Log.d("NEW_STATUS_MF", "${mStatus}")
                mState = states[mStatus] ?: states[ERROR_SERVER]!!
                mState.apply(it)
            }
            catch (e:Exception){
                mStatus = ERROR_APP
                mState = states[mStatus] ?: states[ERROR_SERVER]!!
                mState.apply(it)
            }
        }
    }

    //region LIFECYCLE
    //======================================================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupsSpinner()
        setupObserver()
        txtUrl.setText(URL)
        completedUDAsViews = listOf(ivUda1Completed, ivUda2Completed, ivUda3Completed, ivUda4Completed, ivUda5Completed)
        isPolling = true    // to set init status I enable status-updating in the observer
        viewModel.status.value = Status(STATUS_SUCCESS, RESET)
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
                viewModel.put(mGroupId, mExplorerId, status, data)
    }

    // WAIT_APP State propose a groupId
    fun insertGroupID(grp_id:Int, expl_id:Int = -1){
        if(grp_id == -1){
            return
        }
        mGroupId    = grp_id
        mExplorerId = expl_id
        if(checkConnection())        viewModel.put(grp_id, expl_id, GROUP_SENT)
    }

    fun startPolling(){

        if(checkConnection()){
            isPolling = true
            viewModel.startPolling(URL)     // viewModel.startPolling(txtUrl.text.toString())
        }
    }

    fun stopPolling(){
        isPolling   = false
        mGroupId    = -1
        mExplorerId = -1
        if(checkConnection())        viewModel.stopPolling()
    }
    //endregion======================================================================

    //region ANSWERS
    //==============================================================================================
    fun showAnswerDialog(jsondata:String, type:Int){

        val bundle = Bundle()
        bundle.putString("data", jsondata)

        mAnswerDF = if(type == QUESTION_TYPE_ARR)   AnswerButtonsDF()
                    else                            AnswerTextDF()

        mAnswerDF?.arguments    = bundle
        mAnswerDF?.isCancelable = false
        mAnswerDF?.show(parentFragmentManager, "Inserisci Risposta")

        requireActivity().supportFragmentManager.setFragmentResultListener(TARGET_FRAGMENT_ANSWER_REQUEST, viewLifecycleOwner) { requestKey, bundl ->
            val answer = bundl.getString("answer") ?: ""
            put(DATA_SENT, answer)
            mAnswerDF = null
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(TARGET_FRAGMENT_PAUSE_REQUEST, viewLifecycleOwner) { requestKey, bundl ->
            put(PAUSE)
            mAnswerDF = null
        }
    }
    //endregion=====================================================================================

    //region STATES_CALLBACK =======================================================================
    public fun setUDASubject(subject:String){

        mSubjectName = subject.toLowerCase()
        val intent = Intent("SUBJECT_UPDATE")
        intent.putExtra("data", subject)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

        if(subject.isNotEmpty()) {

            var uri = "${mSubjectName}_elementi"
            ivElements.loadDrawableFromName(uri, requireContext())

            uri     = "${mSubjectName}_uda_spente"
            ivOffIcons.loadDrawableFromName(uri, requireContext())
        }
        else {
            ivElements.visibility = View.INVISIBLE
            ivOffIcons.visibility = View.INVISIBLE
        }
    }

    public fun groupConfirmed(data:String){
        val intent = Intent("GROUP_UPDATE")
        intent.putExtra("data", "REGISTRATO COME GRUPPO $mGroupId")
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

        txtGroup.text = mGroupId.toString()
    }

    public fun blinkUDA2Reach(udaid:String){
        mCurrUdaId = udaid
        var uri = "${mSubjectName}_uda${mCurrUdaId}_accesa"

        ivOnIcons.loadDrawableFromName(uri, requireContext())

        val disposableTimer = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { aLong: Long           ->
                run {
                    blinkFlag = if (blinkFlag == View.VISIBLE)  View.INVISIBLE
                                else                            View.VISIBLE
                    ivOnIcons.visibility = blinkFlag
                }})
        compDispTimer.add(disposableTimer)
    }

    public fun stopBlinking(stopandkeep:Boolean=true){

        if(stopandkeep)     ivOnIcons.visibility = View.VISIBLE
        else                ivOnIcons.setImageDrawable(null)

        compDispTimer.clear()
    }

    public fun udaCompleted(){
        val uda_iv      = completedUDAsViews[mCurrUdaId.toInt()-1]
        val res_name    = "${mSubjectName}_uda${mCurrUdaId}_completata"

        uda_iv.loadDrawableFromName(res_name, requireContext())

    }
    //endregion=====================================================================================

    //region ACCESSORY
    //==============================================================================================
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

    public fun getUrl():String{
        return txtUrl.text.toString()
    }
    //endregion======================================================================
}