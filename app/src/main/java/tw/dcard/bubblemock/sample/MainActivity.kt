package tw.dcard.bubblemock.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tw.dcard.bubblemock.Calculator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Calculator().add(1,2)
    }
}
