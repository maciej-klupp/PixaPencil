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

package com.therealbluepandabear.pixapencil.fragments.easteregg

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.activities.main.MainActivity
import com.therealbluepandabear.pixapencil.databinding.FragmentEasterEggBinding
import com.therealbluepandabear.pixapencil.utility.constants.StringConstants

class EasterEggFragment : Fragment() {
    private var _binding: FragmentEasterEggBinding? = null
    private var showToast: Boolean = true

    val binding get(): FragmentEasterEggBinding {
        return _binding!!
    }

    companion object {
        fun newInstance(): EasterEggFragment {
            return EasterEggFragment()
        }
    }

    private fun showToastIfApplicable() {
        if (requireActivity() is MainActivity) {
            val sharedPreferenceObject = (requireActivity() as MainActivity).sharedPreferenceObject

            if (sharedPreferenceObject.contains(StringConstants.Identifiers.SHARED_PREFERENCE_SHOW_EASTER_EGG_TOAST_IDENTIFIER)) {
                showToast = sharedPreferenceObject.getBoolean(StringConstants.Identifiers.SHARED_PREFERENCE_SHOW_EASTER_EGG_TOAST_IDENTIFIER, showToast)
            } else {
                with (sharedPreferenceObject.edit()) {
                    putBoolean(StringConstants.Identifiers.SHARED_PREFERENCE_SHOW_EASTER_EGG_TOAST_IDENTIFIER, false)
                    apply()
                }
            }
        }

        if (showToast) {
            Toast.makeText(this.requireContext(), getString(R.string.fragmentEasterEgg_toast_text), Toast.LENGTH_LONG).show()
            enterFullscreenMode()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun enterFullscreenMode() {
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setShowHideAnimationEnabled(false)
        supportActionBar?.hide()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun setup() {
        showToastIfApplicable()
        enterFullscreenMode()
    }

    private fun onExit() {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEasterEggBinding.inflate(inflater, container, false)

        setup()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onExit()
        _binding = null
    }
}