package com.esmaeel.softask.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.esmaeel.softask.R
import com.esmaeel.softask.data.remote.WebService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_hilt.*
import javax.inject.Inject

@AndroidEntryPoint
class HiltActivity : AppCompatActivity() {
//    @Inject
//    lateinit var mService: WebService


    private val hiltCarsViewModel: HiltCarsViewModel by viewModels()
    private var page = 1

    private val TAG = "HiltActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilt)

        hiltCarsViewModel.getCarsLiveData().observe(this, Observer {
            Toast.makeText(applicationContext, it.data.toString(), Toast.LENGTH_SHORT)
                .show()
        })

        hiltCarsViewModel.getErrorMessageLiveData().observe(this, Observer {
            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                .show()
        })

        butn.setOnClickListener {
            hiltCarsViewModel.getCars(page)
            page++
        }
    }
}