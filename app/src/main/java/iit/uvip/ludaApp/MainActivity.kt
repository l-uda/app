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


class VersionResponse(json: String) : JSONObject(json) {
    val version: String? = this.optString("version")
    val url: String? = this.optString("url")
}

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val TEST_PERMISSIONS_REQUEST_INTERNET = 2

    private var dialog: AlertDialog? = null

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
            runOnUiThread {
                var remote_version = VersionResponse(json)
                Log.d("VERSION", getVersionName(this))
                if (getVersionName(this) != remote_version.version) {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(server_url + "app.apk"))
                    startActivity(browserIntent)
                }
            }
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
