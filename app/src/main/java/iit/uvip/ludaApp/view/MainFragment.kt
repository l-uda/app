package iit.uvip.ludaApp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels

import iit.uvip.ludaApp.R
import iit.uvip.ludaApp.model.DependenciesProviderParam
import iit.uvip.ludaApp.model.RemoteConnector
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
import iit.uvip.ludaApp.model.RemoteConnector.Companion.FINALIZED
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_ERROR
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_SUCCESS
import iit.uvip.ludaApp.model.Status
import iit.uvip.ludaApp.viewmodel.StatusVM
import kotlinx.android.synthetic.main.fragment_main.*

import org.albaspazio.core.accessory.isOnline
import org.albaspazio.core.fragments.BaseFragment
import org.albaspazio.core.ui.show1MethodDialog
import org.albaspazio.core.ui.showAlert


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

START_STATUS (internal status not given by the server)
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
        const val START_STATUS = -1
    }

    override val LOG_TAG = MainFragment::class.java.simpleName

    private val viewModelFactory = StatusVM.Factory(this, null, DependenciesProviderParam.getInstance(URL).remoteConnector)
    private val viewModel:StatusVM by activityViewModels { viewModelFactory }

    private var mGroupId:Int = -1
    private var mStatus:Int = -1
    private var mPressStatus:Int = -1   // status to be sent when pressing action button
    private var mActionStatus:Int = -1
    private var mIsOnline:Boolean   = false

    // stores : status message, action button label, new status after button press.
    private val messages:HashMap<Int, Triple<String, String, Int>> by lazy {  hashMapOf(
        START_STATUS    to Triple(resources.getString(R.string.status_start)       , resources.getString(R.string.action_start)       , NO_STATUS),
        IDLE            to Triple(resources.getString(R.string.status_idle)       , resources.getString(R.string.action_idle)       , NO_STATUS),
        WAIT_APP        to Triple(resources.getString(R.string.status_wait_app)   , resources.getString(R.string.action_wait_app)   , GROUP_SENT),
        GROUP_SENT      to Triple(resources.getString(R.string.status_group_sent) , resources.getString(R.string.action_group_sent) , NO_STATUS),
        REACH_UDA       to Triple(resources.getString(R.string.status_reach_uda)  , resources.getString(R.string.action_reach_uda)  , REACHING_UDA),
        REACHING_UDA    to Triple(resources.getString(R.string.status_reaching_uda),resources.getString(R.string.action_reaching_uda),READY),
        READY           to Triple(resources.getString(R.string.status_ready)      , resources.getString(R.string.action_ready)      , REACHING_UDA),
        STARTED         to Triple(resources.getString(R.string.status_started)    , resources.getString(R.string.action_started)    , PAUSE),
        PAUSED          to Triple(resources.getString(R.string.status_paused)     , resources.getString(R.string.action_paused)     , RESUME),
        WAIT_DATA       to Triple(resources.getString(R.string.status_wait_data)  , resources.getString(R.string.action_wait_data)  , DATA_SENT),
        DATA_SENT       to Triple(resources.getString(R.string.status_data_sent)  , resources.getString(R.string.action_data_sent)  , NO_STATUS),
        COMPLETED       to Triple(resources.getString(R.string.status_completed)  , resources.getString(R.string.action_completed)  , NO_STATUS),
        FINALIZED       to Triple(resources.getString(R.string.status_finalized)  , resources.getString(R.string.action_finalized)  , NO_STATUS)
//        RESUME          to Triple(resources.getString(R.string.stat),),
//        ABORTED         to Triple(resources.getString(R.string.network_absent),),
//        RESTART         to Triple(resources.getString(R.string.network_absent),),
    )
    }

    //==============================================================================================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupsSpinner()
        setupObserver()
        viewModel.status.value = Status(STATUS_SUCCESS, START_STATUS, "")
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        checkConnection()
    }

    //==============================================================================================================
    // BUSINESS LOGIC
    //==============================================================================================================
    private fun setupObserver(){

        viewModel.status.observe(viewLifecycleOwner) {
            when(it.result) {

                STATUS_SUCCESS -> {

                    mStatus     = it.status
                    val data    = it.data

                    Log.d("Main", "new status $mStatus")
                    updateButtonAction(mStatus)
                    updateComponentsVisibility(mStatus)
                    updateUITexts(mStatus, messages[it.status] ?: Triple("","",-1), data)

                    when(mStatus){

                        START_STATUS    -> {}   // app init or after user cancel session search. show only start button
                                                // on click: start polling
                        IDLE            -> {}   // no server session exist, go on looking for a valid session
                                                // on click: stop polling, reset App
                        WAIT_APP        -> {}   // server session initialized, show INSERT GROUP FORM
                                                // on click: send group
                        GROUP_SENT      -> {    // user sent ID, server may answer: ok => show "GROUP SENT" and wait for REACH_UDA, ko: show "INSERT AGAIN group id"
                            if(data == -1)
                                show1MethodDialog(requireActivity(), resources.getString(R.string.warning),resources.getString(R.string.group_wrong)) {
                                    viewModel.status.value = Status(STATUS_SUCCESS, WAIT_APP, "")
                                }
                            else
                                mGroupId = data.toString().toInt()

                        }
                        REACH_UDA       -> {    mPressStatus = REACHING_UDA }   // server specify next uda, show "REACH UDA #X, press button to confirm", update button text
                        REACHING_UDA    -> {    mPressStatus = READY        }   // user confirmed that is reaching uda, show "press button I'm ready when user physically reach the uda"
                        READY           -> {    mPressStatus = REACHING_UDA }   // user told he is ready. show "waiting for uda start", button send app back to REACHING_UDA
                        STARTED         -> {    mPressStatus = PAUSE }          // show started
                        PAUSE           -> {}
                        PAUSED          -> {    mPressStatus = RESTART }        // show paused
                        RESUME          -> {}
                        RESTART         -> {}
                        WAIT_DATA       -> {}    //  show "insert answer" form
                        DATA_SENT       -> {}    //  show sending data
                        COMPLETED       -> {}    //  show completed
                        FINALIZED       -> {}    //  show finalized
                    }
                }
                STATUS_ERROR -> {
                    updateUITexts(-1, Triple("errore stato : $it", "", NO_STATUS))
                }
            }
        }
    }

    //======================================================================
    // UI UPDATES & INTERACTIONS
    //======================================================================
    private fun updateUITexts(status:Int, text:Triple<String, String, Int>, data:Any?=null){

        txtStatus.text = text.first

        if(text.second.isNullOrEmpty())
            btAction.visibility = View.INVISIBLE
        else {
            btAction.text       = text.second
            btAction.visibility = View.VISIBLE
        }

        mActionStatus = text.third

        when(status){
            GROUP_SENT      -> {
                val d = data as Int
                txtGroup.text = if(d == -1)         resources.getString(R.string.group_wrong)
                                else                d.toString() // resources.getString(R.string.group_defined, d.toString())
            }
            REACH_UDA       -> txtUDA.text = data.toString()
        }

    }

    private fun updateComponentsVisibility(status:Int){
        when(status){
            START_STATUS -> {
                txtStatus.visibility    = View.INVISIBLE
                txtGroup.visibility     = View.INVISIBLE
                txtUDA.visibility       = View.INVISIBLE
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.INVISIBLE
            }
            IDLE -> {
                txtStatus.visibility    = View.VISIBLE      // <-----
                txtGroup.visibility     = View.INVISIBLE
                txtUDA.visibility       = View.INVISIBLE
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.INVISIBLE
            }
            WAIT_APP -> {
                txtStatus.visibility    = View.VISIBLE
                txtGroup.visibility     = View.INVISIBLE
                txtUDA.visibility       = View.INVISIBLE
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.VISIBLE      // <-----
            }
            GROUP_SENT -> {     // this is given when GROUP_SENT put is successfull
                txtStatus.visibility    = View.INVISIBLE
                txtGroup.visibility     = View.VISIBLE      // <-----
                txtUDA.visibility       = View.INVISIBLE
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.INVISIBLE    // <-----
            }
            REACH_UDA   ->  {
                txtStatus.visibility    = View.VISIBLE
                txtGroup.visibility     = View.VISIBLE
                txtUDA.visibility       = View.VISIBLE      // <-----
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.INVISIBLE
            }
            WAIT_DATA -> {
                txtStatus.visibility    = View.VISIBLE
                txtGroup.visibility     = View.VISIBLE
                txtUDA.visibility       = View.VISIBLE
                txtResult.visibility    = View.VISIBLE      // <-----
                spinner.visibility      = View.INVISIBLE
            }

            else -> {
                txtStatus.visibility    = View.VISIBLE
                txtGroup.visibility     = View.VISIBLE
                txtUDA.visibility       = View.VISIBLE
                txtResult.visibility    = View.INVISIBLE
                spinner.visibility      = View.INVISIBLE
            }
        }
    }

    // put status, put status+data or nothing
    private fun updateButtonAction(status:Int){

        btAction.visibility = View.VISIBLE
        when(status){
            START_STATUS -> {
                btAction.setOnClickListener{
                    startPolling()
                    viewModel.status.value = Status(STATUS_SUCCESS, START_STATUS, "")
                }
            }

            IDLE -> {
                btAction.setOnClickListener{
                    stopPolling()
                    viewModel.status.value = Status(STATUS_SUCCESS, START_STATUS, "")
                }
            }
            WAIT_APP -> {
                btAction.setOnClickListener{
                    insertGroupID(spinner.selectedItemPosition+1)
                }
            }
            REACH_UDA, REACHING_UDA, READY, STARTED, PAUSED -> {
                btAction.setOnClickListener{
                    put(mPressStatus)
                }
            }
            WAIT_DATA -> {
                btAction.setOnClickListener{
                    viewModel.put(mGroupId, DATA_SENT, "")
                }
            }
            RESUME, RESTART, PAUSE -> {
                btAction.setOnClickListener{}
                btAction.visibility = View.INVISIBLE
            }
            else ->{
                btAction.setOnClickListener{
                    put(status)
                }
            }

        }
    }
    //======================================================================
    //region REMOTE CALLS
    //======================================================================
    private fun put(status:Int, data:String = ""){
        if(checkConnection())
            if(status != NO_STATUS)
                viewModel.put(mGroupId, status, data)
    }

    private fun insertGroupID(grp_id:Int){
        if(checkConnection())        viewModel.setGroupID(grp_id)
    }

    private fun startPolling(groupId:Int = -1){
        if(checkConnection())        viewModel.startPolling()
    }

    private fun stopPolling(groupId:Int = -1){
        if(checkConnection())        viewModel.stopPolling()
    }
    //endregion======================================================================

    //======================================================================
    //region ACCESSORY
    //======================================================================
    private fun initGroupsSpinner(){
        ArrayAdapter.createFromResource(requireContext(), R.array.groups_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
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