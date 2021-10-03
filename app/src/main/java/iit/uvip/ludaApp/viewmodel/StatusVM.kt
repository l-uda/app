package iit.uvip.ludaApp.viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

import iit.uvip.ludaApp.model.RemoteConnector
import iit.uvip.ludaApp.model.RemoteConnector.Companion.ERROR_SERVER
import iit.uvip.ludaApp.model.RemoteConnector.Companion.IDLE
import iit.uvip.ludaApp.model.RemoteConnector.Companion.RESET
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_ERROR
import iit.uvip.ludaApp.model.RemoteConnector.Companion.STATUS_SUCCESS
import iit.uvip.ludaApp.model.RemoteConnector.Companion.WAIT_APP
import iit.uvip.ludaApp.model.Status
import iit.uvip.ludaApp.view.MainFragment.Companion.ERROR_APP_NOT_ASSOCIATED

// here I check whether new status is different from current one
// in that case, I change status (which is a MutableLiveData<Status> observed in the GUI)
class StatusVM( private val savedStateHandle: SavedStateHandle,
                private val remoteConnector: RemoteConnector) : ViewModel() {

    companion object {
        const val GROUP_ID  = "GROUP_ID"
        const val STATUS    = "STATUS"

    }

    private val disposable = CompositeDisposable()

    private var groupId:Int = savedStateHandle.get<Int>(GROUP_ID) ?: -1
        get() = savedStateHandle.get<Int>(GROUP_ID) ?: -1
        set(value) {
            savedStateHandle.set(GROUP_ID, value)
            field = value
        }

    private var statusId:Int = savedStateHandle.get<Int>(STATUS) ?: -1
        get() = savedStateHandle.get<Int>(STATUS) ?: -1
        set(value) {
            savedStateHandle.set(STATUS, value)
            field = value
        }

    val status = MutableLiveData<Status>()

    init {
        remoteConnector.newServerEvent
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if(it.result == STATUS_SUCCESS) {
                if (it.status != statusId) {            // NEW STATUS RECEIVED

                    if(it.status > IDLE && groupId == -1)   // c'è una sessione in corso, ma l'app non è collegata a nessun gruppo => chiedi associazione
                        it.status = WAIT_APP

                    statusId        = it.status
                    status.value    = it
                }
            }
            else{
                statusId        = ERROR_SERVER
                status.value    = Status(STATUS_ERROR, ERROR_SERVER, it.data)
            }
        }
        .addTo(disposable)
    }

    //======================================================================
    // put
    //======================================================================
    fun put(grp_id: Int, status_code:Int, data:String = "") {

        if(grp_id == -1){
            statusId        = ERROR_APP_NOT_ASSOCIATED
            status.value    = Status(STATUS_ERROR, statusId)
            return
        }
        remoteConnector.put(grp_id, status_code, data)
    }

    fun setGroupID(grp_id:Int){
        groupId = grp_id
        remoteConnector.setGroupID(grp_id)
    }

    //======================================================================
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        remoteConnector.clear()
    }
    //======================================================================
    // polling
    //======================================================================
    fun startPolling() {
        remoteConnector.startPolling(groupId)
    }

    fun stopPolling() {
        groupId = -1
        remoteConnector.stopPolling()
        statusId = RESET
    }
    //======================================================================
    class Factory(
        owner: SavedStateRegistryOwner,
        defaultState: Bundle?,
        private val remoteConnector: RemoteConnector
    ) : AbstractSavedStateViewModelFactory(owner, defaultState) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return StatusVM(handle, remoteConnector) as T
        }
    }
}




//        @JvmStatic val STATUS_DISCONNECTED  = 10    // App disconnessa
//        @JvmStatic val STATUS_CONNECTING    = 11    // sessione presente, gruppo inserito, in attesa di connessione
//        @JvmStatic val STATUS_CONNECTED     = 12    // sessione presente, gruppo inserito, connesso
//        @JvmStatic val STATUS_UDA_STARTED   = 1     // uda partita
//        @JvmStatic val STATUS_UDA_ABORTED   = 2     // uda abortita
//        @JvmStatic val STATUS_UDA_PAUSED    = 3     // uda in pausa
//        @JvmStatic val STATUS_UDA_RESUMED   = 4     // uda riavviata
//        @JvmStatic val STATUS_UDA_COMPLETED = 5     // uda finita
//        @JvmStatic val STATUS_UDA_FINALIZED = 6     // tutte le uda finite, tutti al portale
//        @JvmStatic val STATUS_UDA_FINISHED  = 7     // sessione terminata