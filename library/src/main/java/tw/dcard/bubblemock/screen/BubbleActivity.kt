package tw.dcard.bubblemock.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tw.dcard.bubblemock.R

/**
 * @author Batu
 */
class BubbleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BubbleMainFragment.newInstance())
                .commit()
        }
    }
}