package com.realtomjoney.pyxlmoose.fragments

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.realtomjoney.pyxlmoose.activities.canvas.canvasFragmentInstance
import com.realtomjoney.pyxlmoose.activities.canvas.data
import com.realtomjoney.pyxlmoose.activities.canvas.index
import com.realtomjoney.pyxlmoose.activities.canvas.pixelDataAsViews
import com.realtomjoney.pyxlmoose.listeners.CanvasFragmentListener
import com.realtomjoney.pyxlmoose.adapters.CanvasRecyclerAdapter
import com.realtomjoney.pyxlmoose.databinding.FragmentCanvasBinding

class CanvasFragment(private val spanCount: Int,
                     private val isGridVisible: Boolean,
                     private val savedGridState: List<View>? = null) : Fragment() {
    private var _binding: FragmentCanvasBinding? = null

    private val binding get() = _binding!!

    private lateinit var caller: CanvasFragmentListener

    companion object {
        fun newInstance(spanCount: Int, isGridVisible: Boolean, savedGridState: List<View>?) = CanvasFragment(spanCount, isGridVisible, savedGridState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CanvasFragmentListener) caller = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCanvasBinding.inflate(inflater, container, false)
        setUpRecyclerView()

        if (index != -1) {
            data.forEach { data_it ->
                val index = data.indexOf(data_it)

                if (pixelDataAsViews[index].background != null) data_it.setBackgroundColor((pixelDataAsViews[index].background as ColorDrawable).color)
            }
            this.modifyPixels(data)
        }

        return binding.root
    }

    private fun getPixelData() = savedGridState ?: caller.initPixels()

    private fun setUpRecyclerView() {
        with (binding) {
            canvasRecyclerView.layoutManager = GridLayoutManager(activity as Context, spanCount)
            canvasRecyclerView.adapter = CanvasRecyclerAdapter(getPixelData(), caller, isGridVisible)
            canvasRecyclerView.suppressLayout(true)
        }
    }

    fun modifyPixels(newData: List<View>) {
        with (binding) {
            canvasRecyclerView.layoutManager = GridLayoutManager(activity as Context, spanCount)
            canvasRecyclerView.adapter = CanvasRecyclerAdapter(newData, caller, isGridVisible)
            canvasRecyclerView.suppressLayout(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}