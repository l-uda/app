package iit.uvip.ludaApp


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import iit.uvip.ludaApp.BuildConfig.server_url
import kotlinx.android.synthetic.main.activity_main.*
import org.albaspazio.core.accessory.getVersionName
import org.albaspazio.core.fragments.BaseFragment
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import java.nio.file.Files
import java.nio.file.Paths
import android.widget.Toast

import androidx.test.core.app.ApplicationProvider.getApplicationContext

import android.content.ActivityNotFoundException
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import java.io.File
import android.app.DownloadManager
import android.provider.Settings
import androidx.core.content.ContentProviderCompat.requireContext




class VersionResponse(json: String) : JSONObject(json) {
    val version: String? = this.optString("version")
    val url: String? = this.optString("url")
}



class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val TEST_PERMISSIONS_REQUEST_INTERNET = 2

    private var dialog: AlertDialog? = null


    private fun InstallAPK(file :File) {
        Log.d("install", file.toString())
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(applicationContext, file),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Log.e("LUDA Update", "Error in opening the file!")
            }
        } else {
            Log.d("error", "File does not exists")
            Toast.makeText(applicationContext, "Install Failed", Toast.LENGTH_LONG).show()
        }
    }

    fun uriFromFile(context: Context?, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context!!, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }
    // This will be called whenever an Intent with an action named "NAVIGATION_UPDATE" is broadcasted.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when(intent.action) {
                "NAVIGATION_UPDATE" -> refreshNavigationVisibility()
                "GROUP_UPDATE"  -> {
                    val text = intent.getStringExtra("data")
                    refreshActionBarGroup(text ?: "error")
                }
                "SUBJECT_UPDATE"  -> {
                    val text = intent.getStringExtra("data")
                    refreshActionBarSubject(text ?: "error")  // supportActionBar?.title = text
                }
            }
        }
    }

    private fun isFileExists(filename: String): Boolean {
        val folder1 = File(Environment.getExternalStorageDirectory().absolutePath + filename)
        return folder1.exists()
    }

    fun mydeleteFile(filename: String): Boolean {
        val folder1 = File(Environment.getExternalStorageDirectory().absolutePath + filename)
        return folder1.delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.my_toolbar))
//        setupActionBarWithNavController(findNavController(R.id.my_nav_host_fragment))
        findNavController(R.id.my_nav_host_fragment).addOnDestinationChangedListener(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),TEST_PERMISSIONS_REQUEST_INTERNET)

        thread {
            val json = try { URL(server_url + "version").readText() } catch (e: Exception) { return@thread }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!packageManager.canRequestPackageInstalls()) {
                    startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                            Uri.parse(
                                String.format(
                                    "package:%s",
                                    packageName
                                )
                            )
                        ), 1
                    )
                }
            }
//Storage Permission
//Storage Permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
            stopLockTask()

            runOnUiThread {
                var remote_version = VersionResponse(json)
                Log.d("VERSION", getVersionName(this))
                if (getVersionName(this) != remote_version.version) {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "app.apk"
                    )
                    if (file.exists()) {
                        Log.d("file","esiste")

                        file.delete()
                    }
                    Log.d("file", file.toString())

                    Thread {
                        val request = DownloadManager.Request(Uri.parse(server_url + "app.apk"))
                        request.setDescription("Download Aggiornamento")
                        request.allowScanningByMediaScanner()
                        request.setTitle("app.apk")
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//Set the local destination for the downloaded file to a path within the application's external files directory
//Set the local destination for the downloaded file to a path within the application's external files directory
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            "app.apk"
                        ) //To Store file in External Public Directory use "setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)"
                        (this@MainActivity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
                        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(ctxt: Context, intent: Intent) {
                                // After getting the result
                                runOnUiThread {
                                    // Post the result to the main thread
                                    InstallAPK(file)
                                }
                            }
                        }
                        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                    }.start()
                }
            }
            startLockTask()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLockTask()
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    override fun onResume() {
        super.onResume()
        startLockTask()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("NAVIGATION_UPDATE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("GROUP_UPDATE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("SUBJECT_UPDATE"))
    }

    override fun onSupportNavigateUp() = findNavController(R.id.my_nav_host_fragment).navigateUp()

    override fun onDestinationChanged(controller: NavController, destination: NavDestination,arguments: Bundle?) {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus)
            refreshNavigationVisibility()
    }

    fun refreshNavigationVisibility() {
        val currentFragment = my_nav_host_fragment.childFragmentManager.fragments.firstOrNull() as? BaseFragment

        if (currentFragment?.hideAndroidControls == true) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            actionBar?.hide()
            supportActionBar?.hide()
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            actionBar?.show()
            supportActionBar?.show()
        }
    }

    fun refreshActionBarGroup(text:String){
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val toolbarGroup = toolbar.findViewById<TextView>(R.id.toolbarGroup)
        toolbarGroup.text = text
    }

    fun refreshActionBarSubject(text:String){
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        val toolbarSubject = toolbar.findViewById<TextView>(R.id.toolbarSubject)
        toolbarSubject.text = text
    }

    override fun onBackPressed() {
        return
        /*
        val currentFragment = my_nav_host_fragment.childFragmentManager.fragments.firstOrNull() as? BaseFragment

        when(currentFragment?.LOG_TAG){
            "MainFragment"          -> {
                AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(resources.getString(R.string.close_alert))
                    .setMessage(resources.getString(R.string.close_app_message))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> finish() }
                    .setNegativeButton(resources.getString(R.string.no), null)
                    .show()
            }
            else                    -> super.onBackPressed()
        }
         */
    }

    override fun onDestroy() {

        // release TTS
        val app = application as MainApplication
        app.tts?.shutdown()

        dialog?.dismiss()
        findNavController(R.id.my_nav_host_fragment).removeOnDestinationChangedListener(this)
        super.onDestroy()
    }
}
