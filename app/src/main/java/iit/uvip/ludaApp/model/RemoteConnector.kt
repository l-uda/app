package iit.uvip.ludaApp.model

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RemoteConnector(private val service: UdaService){

    val newServerEvent = PublishRelay.create<Status>()

    private var disposableTimer: Disposable? = null
    private var disposable: Disposable? = null

    private var lastSentStatus:Int  = IDLE
    private var groupId:Int         = -1

    companion object {

        @JvmStatic val STATUS_SUCCESS       = 100    //
        @JvmStatic val STATUS_ERROR         = 101    //
        @JvmStatic val TIMER_ERROR          = 102    //

        // STATUS GET

        const val RESET         = -1    //  when server is rebooted or re-inited
        const val IDLE          = 0     //  get <- (-1)
        const val WAIT_APP      = 1     //  get <- (-1)
        const val REACH_UDA     = 3     //  status, data <- get(grpid)
        const val READY         = 5     //  status <- get(grpid)
        const val STARTED       = 7     //  status <- get(grpid)
        const val PAUSED        = 9     //  status <- get(grpid)
        const val ABORTED       = 12    //  status <- get(grpid)
        const val WAIT_DATA     = 14    //  status <- get(grpid)
        const val COMPLETED     = 16    //  status <- get(grpid)
        const val FINALIZED     = 18    //  status <- get(grpid)
        const val WAIT_SERVER   = 19    //  status <- get(grpid)
        const val ERROR_UDA     = 20    //  status <- get(grpid)
        const val ERROR_SERVER  = 22    //  status <- get(grpid)

        const val RECEIVED_UDA_ID = 103 // this is returned after a GROUP_SENT
                                        // used to get associated uda when APP reconnect after a crash


        // STATUS PUT
        const val GROUP_SENT    = 2     //  ---> put(data , status)
        const val REACHING_UDA  = 4     //  ---> put(grpid, status)
        const val PAUSE         = 8     //  ---> put(grpid, status)
        const val RESUME        = 10    //  ---> put(grpid, status)
        const val ABORT         = 11    //  ---> put(grpid, status)
        const val RESTART       = 13    //  ---> put(grpid, status)
        const val DATA_SENT     = 15    //  ---> put(grpid, status, data)
        const val ERROR_APP     = 21    //  ---> put(grpid, status, data)

        // STATUS UNMANAGED
        const val START         = 6     //  the ADMIN send it to UDAs that respond with a STARTED
        const val FINALIZE      = 17    //  .......


    }
    //============================================================================================
    // MANAGE POLLING
    //============================================================================================
    fun startPolling(grp_id: Int){
        groupId = grp_id
        disposableTimer = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { aLong: Long           -> getStatus(groupId) },
                        { throwable: Throwable  -> processError(TIMER_ERROR, TIMER_ERROR, throwable.message) })
    }

    fun stopPolling() {
        groupId = -1
        disposableTimer?.dispose()
        disposableTimer = null
    }
    //============================================================================================
    // GET STATUS
    //============================================================================================
    private fun getStatus(luda_id: Int) {
        disposable = service.getStatus(luda_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { result -> newServerEvent.accept(Status(STATUS_SUCCESS, result.status?.toString()?.toInt() ?: IDLE, result.data)) },
                        { error  -> processError(STATUS_ERROR, STATUS_ERROR, error.message) })
    }
    //============================================================================================
    // PUT STATUS / DATA
    //============================================================================================
    fun setGroupID(grp_id: Int) {
        disposable = service.putStatus(grp_id, GROUP_SENT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    run {
                        groupId = grp_id
                        newServerEvent.accept(Status(STATUS_SUCCESS, GROUP_SENT, grp_id.toString()))
                        newServerEvent.accept(Status(STATUS_SUCCESS, RECEIVED_UDA_ID, it.data))
                    }
                },
                { error ->  processError(STATUS_ERROR, GROUP_SENT, error.message) })
    }
    //============================================================================================
    fun put(grp_id: Int, status:Int, data:String = ""){
        lastSentStatus = status
        disposable = service.putStatus(grp_id, status, data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { result -> newServerEvent.accept(Status(STATUS_SUCCESS, result.status?.toString()?.toInt() ?: -1, result.data)) },
                        { error ->  processError(STATUS_ERROR, lastSentStatus, error.message) })
    }
    //============================================================================================
    // ACCESSORY
    //============================================================================================
    private fun processError(code:Int, status:Int, msg: String?){
        newServerEvent.accept(Status(code, status, (msg ?: "")))
    }

    fun clear(){
        disposable?.dispose()
        disposableTimer?.dispose()
    }
    //============================================================================================
}