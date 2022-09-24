package iit.uvip.ludaApp.model

import android.util.Log
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class RemoteConnector{

    val newServerEvent = PublishRelay.create<Status>()

    private var disposableTimer: Job? = null
    private var disposable: Disposable? = null
    private var isPolling = false

    private var lastSentStatus:Int  = IDLE
    private var groupId:Int         = -1
    private var explorerId:Int      = -1
    private var polling: Int        = -1

    private var service:UdaService? = null

    companion object {

        @JvmStatic val STATUS_SUCCESS       = 100    //
        @JvmStatic val STATUS_ERROR         = 101    //
        @JvmStatic val TIMER_ERROR          = 102    //

        @JvmStatic val USER_ALREADY_EXIST   = 1001    //

        // STATUS GET
        const val RESET         = -2    //  when not polling.
        const val POLLING       = -1    //  user pressed "connect"
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

        // STATUS PUT
        const val GROUP_SENT    = 2     //  ---> put(data , status)
//        const val REACHING_UDA  = 4     //  ---> put(grpid, status)
        const val PAUSE         = 8     //  ---> put(grpid, status)
        const val RESUME        = 10    //  ---> put(grpid, status)
        const val ABORT         = 11    //  ---> put(grpid, status)
        const val RESTART       = 13    //  ---> put(grpid, status)
        const val DATA_SENT     = 15    //  ---> put(grpid, status, data)
        const val ERROR_APP     = 21    //  ---> put(grpid, status, data)

        // STATUS UNMANAGED
//        const val START         = 6     //  the ADMIN send it to UDAs that respond with a STARTED
//        const val FINALIZE      = 17    //  .......


    }

    //============================================================================================
    // PUBLIC FUNCTIONS
    //============================================================================================
    fun startPolling(url: String){

        service         = UdaService.create(url)

        disposableTimer = GlobalScope.launch {
            isPolling = true
            getStatus()
        }
    }

    fun stopPolling() {
        groupId = -1
        explorerId = -1
        polling = -1
        disposableTimer?.cancel()
        disposableTimer = null
        isPolling = false
    }

    fun clear(){
        groupId = -1
        explorerId = -1
        polling = -1
        disposable?.dispose()
        disposableTimer?.cancel()
        isPolling = false
    }

    fun put(grp_id: Int, expl_id: Int, status:Int, data:String = ""){
        if(status == GROUP_SENT)    setGroupID(grp_id, expl_id)
        else {
            lastSentStatus = status
            disposable = service?.putStatus(grp_id, expl_id, status, data)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ result -> newServerEvent.accept(Status(STATUS_SUCCESS, result.status?.toString()?.toInt() ?: -1, result.uda_id ?: listOf(), result.data ?: ""))},
                            { error  -> processError(STATUS_ERROR, lastSentStatus, error.message ?: "") })
        }
    }
    //============================================================================================
    // SET GROUP
    private fun setGroupID(grp_id: Int, expl_id:Int = -1) {
        disposable = service?.putStatus(grp_id, expl_id, GROUP_SENT)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({
                    run {
                        Log.d("REMOTE_CONN", "status: $it")
                        groupId     = grp_id
                        explorerId  = expl_id
                        // communicates completed udas, reach_uda comes in the next "get"
                        newServerEvent.accept(Status(STATUS_SUCCESS, GROUP_SENT, it.uda_id ?: listOf(), it.data ?: "", it.hint ?: listOf()))

//                        newServerEvent.accept(Status(STATUS_SUCCESS, REACH_UDA, it.uda_id ?: listOf(), it.data ?: "", it.indizi ?: listOf()))
                    }
                },
                { error ->  run {
                    val error_message = error.message ?: ""
                    if(!error_message.contains("timeout") && !error_message.contains("timed out")) processError(STATUS_ERROR, GROUP_SENT, error_message)
                } })
    }
    //============================================================================================
    // GET STATUS
    //============================================================================================
    private fun getStatus() {
        var beforeGroupId = groupId
        var beforeExplorerId = explorerId
        disposable = service?.getStatus(groupId, explorerId, polling)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ result ->
                newServerEvent.accept(Status(STATUS_SUCCESS, result.status?.toString()?.toInt() ?: IDLE, result.uda_id ?: listOf(), result.data ?: "",
                result.hint ?: listOf(), ))
                if (beforeGroupId != -1 && beforeExplorerId != -1) {
                    polling = result.revision
                } else {
                    polling = - 1
                    sleep(300)
                }
                if (isPolling) getStatus()
            },
                        { error  ->
                                if (validateError(error.message ?: ""))
                            processError(STATUS_ERROR, STATUS_ERROR, error.message ?: "")
                            if (isPolling) getStatus()
                        })
        }
    //============================================================================================
    // ACCESSORY
    //============================================================================================
    private fun processError(code:Int, status:Int, msg: String = ""){
        newServerEvent.accept(Status(code, status, listOf(), msg))
    }

    // clean error from some cases that we want to ignore
    private fun validateError(msg:String):Boolean{
        return !(msg.contains("Failed to connect", ignoreCase = true) || msg.contains("timeout") || msg.contains("timed out"))

    }
}
