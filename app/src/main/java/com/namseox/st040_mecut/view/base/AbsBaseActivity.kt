package com.namseox.st040_mecut.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.namseox.st040_mecut.utils.SystemUtils
import com.namseox.st040_mecut.utils.showSystemUI


abstract class AbsBaseActivity<V : ViewDataBinding>(var fragment : Boolean) : AppCompatActivity() {
    lateinit var binding: V
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtils.setLocale(this)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        if(fragment){
            navHostFragment =
                supportFragmentManager.findFragmentById(getFragmentID()) as NavHostFragment
            navController = navHostFragment.navController
        }
        init()
    }
    override fun onResume() {
        super.onResume()
        showSystemUI(true)
    }

    override fun onRestart() {
        super.onRestart()
        SystemUtils.setLocale(this)
    }
    abstract fun getFragmentID(): Int
    abstract fun getLayoutId(): Int
    abstract fun init()

}