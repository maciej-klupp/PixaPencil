/*
 * PixaPencil
 * Copyright 2022  therealbluepandabear
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.therealbluepandabear.pixapencil.fragments.appinfo

import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.therealbluepandabear.pixapencil.BuildConfig
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.databinding.FragmentAppInfoBinding
import com.therealbluepandabear.pixapencil.fragments.easteregg.EasterEggFragment


class AppInfoFragment : Fragment() {
    private var _binding: FragmentAppInfoBinding? = null

    val binding get(): FragmentAppInfoBinding {
        return _binding!!
    }

    private var logoTapCount = 0
    private var timerRunning = false

    private val easterEggTimerTask = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timerRunning = true
        }

        override fun onFinish() {
            timerRunning = false
            logoTapCount = 0
        }
    }


    private fun setup() {
        activity?.findViewById<BottomNavigationView>(R.id.activityMain_bottomNavigationView)?.visibility = View.GONE

        binding.fragmentAppInfoAppVersion.text = BuildConfig.VERSION_NAME
        binding.fragmentAppInfoCommunityText.movementMethod = LinkMovementMethod.getInstance()
        binding.fragmentAppInfoCreatedByText.movementMethod = LinkMovementMethod.getInstance()
        binding.fragmentAppInfoAboutText.movementMethod = LinkMovementMethod.getInstance()

        binding.imageView.setOnClickListener {
            if (!timerRunning) {
                easterEggTimerTask.start()
            }

            logoTapCount++

            if (logoTapCount == 5 && timerRunning) {
                childFragmentManager.commit {
                    replace(R.id.fragmentAppInfo_primaryFragmentHost, EasterEggFragment.newInstance())
                    addToBackStack(null)
                }
            }
        }
    }

    companion object {
        fun newInstance(): AppInfoFragment {
            return AppInfoFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.children.forEach {
                    it.isVisible = false
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppInfoBinding.inflate(inflater, container, false)

        setup()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}