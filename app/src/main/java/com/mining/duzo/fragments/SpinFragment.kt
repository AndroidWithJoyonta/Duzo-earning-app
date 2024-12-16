package com.mining.duzo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mygallaray.Admob
import com.example.mygallaray.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mining.duzo.allEarningActivity.ScratchAndEarnActivity
import com.mining.duzo.allEarningActivity.SpinAndEarnActivity
import com.mining.duzo.allEarningActivity.WatchAndEarnActivity
import com.mining.duzo.databinding.FragmentSpinBinding

class SpinFragment : Fragment() {


    lateinit var binding: FragmentSpinBinding

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = firebaseAuth.currentUser



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentSpinBinding.inflate(inflater, container, false)



        Constant.admob = Admob(requireActivity(),false)
        Constant.admob?.setAdUnit("ca-app-pub-1966206743776147~5136407102","ca-app-pub-1966206743776147/8369075100","ca-app-pub-1966206743776147/1803666755","ca-app-pub-1966206743776147/8177503415","ca-app-pub-1966206743776147/3550467469")

        Constant.admob!!.loadNative(binding.frame)

      binding.spinAndEarn.setOnClickListener {


          val intent = Intent(context, SpinAndEarnActivity::class.java)
          startActivity(intent)
      }

        binding.scratchAndEarn.setOnClickListener {


            val intent = Intent(context, ScratchAndEarnActivity::class.java)
            startActivity(intent)
        }

        binding.watchAndEarn.setOnClickListener {


            val intent = Intent(context, WatchAndEarnActivity::class.java)
            startActivity(intent)
        }





        return binding.root



    }





}