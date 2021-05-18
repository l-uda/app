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

        // STATUS PUT
        const val GROUP_SENT    = 2     //  ---> put(data , status)
        const val REACHING_UDA  = 4     //  ---> put(grpid, status)
        const val PAUSE         = 8     //  ---> put(grpid, status)
        const val RESUME        = 10    //  ---> put(grpid, status)
        const val ABORT         = 11    //  ---> put(grpid, status)
        const val RESTART       = 13    //  ---> put(grpid, status)
        const val DATA_SENT     = 15    //  ---> put(grpid, status, data)

        // STATUS UNMANAGED
        const val START         = 6     //  ........
        const val FINALIZE      = 17    //  .......


        const val ERROR_UDA     = 19
        const val ERROR_APP     = 20
        const val ERROR_SERVER  = 21
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
    fun setGroup(grp_id: Int) {

        if(grp_id == RESET){
            groupId = grp_id
            return
        }
        disposable = service.putStatus(grp_id, GROUP_SENT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    run {
                        groupId = grp_id
                        newServerEvent.accept(Status(STATUS_SUCCESS, GROUP_SENT, grp_id))
                    }
                },
                { error ->  processError(STATUS_ERROR, GROUP_SENT, error.message) })
    }
    //============================================================================================
    fun put(grp_id: Int, status:Int, data:String = ""){
        lastSentStatus = DATA_SENT
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