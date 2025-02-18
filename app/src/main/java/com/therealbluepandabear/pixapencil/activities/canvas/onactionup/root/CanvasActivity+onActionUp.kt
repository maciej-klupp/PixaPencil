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

package com.therealbluepandabear.pixapencil.activities.canvas.onactionup.root

import com.therealbluepandabear.pixapencil.activities.canvas.CanvasActivity
import com.therealbluepandabear.pixapencil.activities.canvas.canvascommands.overrideSetPixel
import com.therealbluepandabear.pixapencil.activities.canvas.canvascommands.undo
import com.therealbluepandabear.pixapencil.activities.canvas.getSelectedColor
import com.therealbluepandabear.pixapencil.activities.canvas.judgeUndoRedoStacks
import com.therealbluepandabear.pixapencil.activities.canvas.onactionup.circleToolOnActionUp
import com.therealbluepandabear.pixapencil.activities.canvas.onactionup.ellipseToolOnActionUp
import com.therealbluepandabear.pixapencil.activities.canvas.onactionup.lineToolOnActionUp
import com.therealbluepandabear.pixapencil.activities.canvas.onactionup.rectangleToolOnActionUp
import com.therealbluepandabear.pixapencil.database.BrushesDatabase
import com.therealbluepandabear.pixapencil.enums.SymmetryMode
import com.therealbluepandabear.pixapencil.enums.Tool
import com.therealbluepandabear.pixapencil.enums.ToolFamily
import com.therealbluepandabear.pixapencil.extensions.doAddLast
import com.therealbluepandabear.pixapencil.models.BitmapAction
import com.therealbluepandabear.pixapencil.models.BitmapActionData
import com.therealbluepandabear.pixapencil.models.Coordinate

fun CanvasActivity.resetPreviousCoordinates() {
    binding.activityCanvasDrawingView.prevX = null
    binding.activityCanvasDrawingView.prevY = null
}

fun CanvasActivity.extendedOnActionUp() {
    if (viewModel.currentTool == Tool.ShadingTool) {
        binding.activityCanvasDrawingView.shadingMap.clear()
    }

    when {
        viewModel.currentTool == Tool.LineTool -> {
            lineToolOnActionUp()
        }

        viewModel.currentTool.family == ToolFamily.Rectangle -> {
            rectangleToolOnActionUp()
        }

        viewModel.currentTool == Tool.PolygonTool -> {
            viewModel.undoStack.doAddLast(viewModel.currentBitmapAction!!)
        }

        viewModel.currentTool.family == ToolFamily.Ellipse && (viewModel.currentTool == Tool.CircleTool || viewModel.currentTool == Tool.OutlinedCircleTool) -> {
            circleToolOnActionUp()
        }

        viewModel.currentTool.family == ToolFamily.Ellipse && (viewModel.currentTool == Tool.EllipseTool || viewModel.currentTool == Tool.OutlinedEllipseTool) -> {
            ellipseToolOnActionUp()
        }

        viewModel.currentTool == Tool.EraseTool -> {
            viewModel.undoStack.doAddLast(viewModel.currentBitmapAction!!)

            primaryAlgorithmInfoParameter.color = getSelectedColor()

            resetPreviousCoordinates()
        }

        viewModel.currentTool == Tool.ColorPickerTool -> {

        }

        else -> {
            val isPxPerfect = (binding.activityCanvasDrawingView.pixelPerfectMode && viewModel.currentTool == Tool.PencilTool && (viewModel.currentBrush == BrushesDatabase.toList().first()))

            viewModel.undoStack.doAddLast(viewModel.currentBitmapAction!!)
            resetPreviousCoordinates()

            if (isPxPerfect && viewModel.currentSymmetryMode == SymmetryMode.None) {
                var distinct = viewModel.currentBitmapAction!!.actionData.distinctBy { it.coordinate }
                val data = mutableListOf<BitmapActionData>()

                var c = 0

                while (c < distinct.size) {
                    if (c > 0 && c + 1 < distinct.size
                        && (distinct[c - 1].coordinate.x == distinct[c].coordinate.x || distinct[c - 1].coordinate.y == distinct[c].coordinate.y)
                        && (distinct[c + 1].coordinate.x == distinct[c].coordinate.x || distinct[c + 1].coordinate.y == distinct[c].coordinate.y)
                        && distinct[c - 1].coordinate.x != distinct[c + 1].coordinate.x
                        && distinct[c - 1].coordinate.y != distinct[c + 1].coordinate.y
                    ) {
                        c += 1
                    }

                    data.add(distinct[c])

                    c += 1
                }

                canvasCommandsHelperInstance.undo()

                for (value in data) {
                    distinct = distinct.filter { it == value }
                }

                for (value in data) {
                    canvasCommandsHelperInstance.overrideSetPixel(
                        Coordinate(
                            value.coordinate.x,
                            value.coordinate.y,
                        ),
                        getSelectedColor()
                    )
                }

                viewModel.undoStack.doAddLast(BitmapAction(data))
            }
        }
    }

    judgeUndoRedoStacks()
    viewModel.currentBitmapAction = null
}