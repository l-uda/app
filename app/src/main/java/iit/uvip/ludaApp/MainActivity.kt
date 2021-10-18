package iit.uvip.ludaApp


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.intentfilter.androidpermissions.PermissionManager
import com.intentfilter.androidpermissions.models.DeniedPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.albaspazio.core.fragments.BaseFragment
import org.albaspazio.core.fragments.iNavigated
import java.util.*


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val TEST_PERMISSIONS_REQUEST_INTERNET = 2

    private var dialog: AlertDialog? = null

    // This will be called whenever an Intent with an action named "NAVIGATION_UPDATE" is broadcasted.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(intent.action == "NAVIGATION_UPDATE") refreshNavigationVisibility()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.my_nav_host_fragment))
        findNavController(R.id.my_nav_host_fragment).addOnDestinationChangedListener(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),TEST_PERMISSIONS_REQUEST_INTERNET)
    }

    override fun onPause() {
        super.onPause()
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("NAVIGATION_UPDATE"))
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
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN)

            actionBar?.hide()
            supportActionBar?.hide()
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            actionBar?.show()
            supportActionBar?.show()
        }
    }

    override fun onBackPressed() {
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
