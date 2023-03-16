package com.example.animal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.animal.databinding.HomeTabTwoFragmentBinding

class HomeTapTwoFragment: Fragment() {
    private lateinit var binding: HomeTabTwoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeTabTwoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}