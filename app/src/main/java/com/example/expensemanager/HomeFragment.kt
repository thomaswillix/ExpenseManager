package com.example.expensemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

    //Floating button
    private lateinit var fab_main_btn: FloatingActionButton
    private lateinit var fab_income_btn: FloatingActionButton
    private lateinit var fab_expense_btn: FloatingActionButton

    //Floating button textview
    private lateinit var fab_income_txt: TextView
    private lateinit var fab_expense_txt: TextView

    //Flag
    private var isOpen: Boolean = false

    //Animation
    private lateinit var fadeOpen: Animation
    private lateinit var fadeClose: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView : View = inflater.inflate(R.layout.fragment_home, container,false)

        //Connect floating button to layout
        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn)
        fab_income_btn =myView.findViewById(R.id.income_Ft_btn)
        fab_expense_btn =myView.findViewById(R.id.expense_Ft_btn)

        //Connect floating text to layout
        fab_income_txt = myView.findViewById(R.id.income_ft_text)
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text)

        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        fab_main_btn.setOnClickListener {
            if (isOpen){
                fab_income_btn.startAnimation(fadeClose)
                fab_expense_btn.startAnimation(fadeClose)
                fab_income_btn.isClickable = false
                fab_expense_btn.isClickable = false

                fab_income_txt.startAnimation(fadeClose)
                fab_expense_txt.startAnimation(fadeClose)
                fab_income_txt.isClickable = false
                fab_expense_txt.isClickable = false
                isOpen = false
            } else{
                fab_income_btn.startAnimation(fadeOpen)
                fab_expense_btn.startAnimation(fadeOpen)
                fab_income_btn.isClickable = true
                fab_expense_btn.isClickable = true

                fab_income_txt.startAnimation(fadeOpen)
                fab_expense_txt.startAnimation(fadeOpen)
                fab_income_txt.isClickable = true
                fab_expense_txt.isClickable = true
                isOpen = true
            }
        }
        return  myView
    }


}