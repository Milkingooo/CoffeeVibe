package com.example.coffeevibe.Fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeevibe.Adapters.MenuAdapter
import com.example.coffeevibe.Adapters.MenuAdapter.Companion.VIEW_TYPE_HEADER
import com.example.coffeevibe.Adapters.MenuAdapter.Companion.VIEW_TYPE_ITEM
import com.example.coffeevibe.MenuItem
import com.example.coffeevibe.MenuItemUI
import com.example.coffeevibe.OrderViewModel
import com.example.coffeevibe.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var chipGroup: ChipGroup
    private var categoryPositions = mutableMapOf<String, Int>()
    private lateinit var orderVm : OrderViewModel

    private lateinit var bd: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bd = FirebaseFirestore.getInstance()

        try {
            orderVm = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        }
        catch (e: Exception){
            Log.d("ViewModel", e.message.toString())
        }

        // Инициализация ChipGroup для категорий
        chipGroup = view.findViewById(R.id.chipGroup)
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = MenuAdapter(mutableListOf())
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    VIEW_TYPE_HEADER -> 2
                    VIEW_TYPE_ITEM -> 1
                    else -> 1

                }
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter


        // Загружаем все данные сразу
        loadAllMenuItems()
        adapter.setOrderViewModel(orderVm)
    }

    private fun loadAllMenuItems() {
        // Загружаем все элементы из Firestore
        bd.collection("menu").get()
            .addOnSuccessListener { result ->
                val menuItems = mutableListOf<MenuItemUI>()

                // Группируем элементы по категориям
                val groupedData = result.documents.groupBy { it.getString("category") }

                // Проходим по всем категориям и добавляем их в список
                var currentPosition = 0
                groupedData.forEach { (category, items) ->
                    category?.let {
                        categoryPositions[it] = currentPosition
                    }

                    // Добавляем заголовок категории
                    menuItems.add(MenuItemUI.Header(category ?: "Без категории"))
                    currentPosition++

                    // Добавляем элементы меню
                    items.forEach { document ->
                        val name = document.getString("name") ?: ""
                        val price = document.getString("price") ?: ""
                        var imageUrl = document.getString("image")?.trim() ?: ""
                        imageUrl = transformGoogleDriveLink(imageUrl)
                        menuItems.add(MenuItemUI.Item(name, extractPrice(price).toString(), imageUrl))
                        currentPosition++
                    }
                }

                // Обновляем адаптер с полученными данными
                adapter.submitList(menuItems)

                // Создаём чипы для категорий
                createCategoryChips(categoryPositions.keys.toList())
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Ошибка загрузки данных", exception)
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createCategoryChips(categories: List<String>) {
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category
                textSize = 20f
                chipBackgroundColor = getColorStateList(requireContext(), R.color.rizhi)
                setTextColor(getColor(requireContext(), R.color.black))
                isCheckable = true
                setPadding(10, 10, 10, 10)
                layoutParams = ChipGroup.LayoutParams(
                    ChipGroup.LayoutParams.WRAP_CONTENT,
                    ChipGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // Добавляем чип в ChipGroup
            chipGroup.addView(chip)
        }

        // По умолчанию выделяем первый чип
        if (chipGroup.childCount > 0) {
            (chipGroup.getChildAt(0) as? Chip)?.isChecked = true
        }

        // Обработчик выбора категории
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedChip = group.findViewById<Chip>(checkedId)
            if (checkedChip != null) {
                val category = checkedChip.text.toString()
                scrollToCategory(category)
            }
        }
    }

    private fun scrollToCategory(category: String) {
        val position = categoryPositions[category]
        if (position != null) {
            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val offset =
                (position % layoutManager.spanCount) * recyclerView.width / layoutManager.spanCount
            recyclerView.smoothScrollToPosition(position)
            //layoutManager.scrollToPositionWithOffset(position, offset)
            //Toast.makeText(requireContext(), "Scrolling to position $position", Toast.LENGTH_SHORT).show()
        }
    }

    fun transformGoogleDriveLink(link: String): String {
        val fileId = link.substringAfter("/d/").substringBefore("/view")
        return if (fileId.isEmpty() || fileId.isBlank() || !link.startsWith("https://drive.google.com/file/d/")) {
            ""
        } else "https://drive.google.com/uc?id=$fileId"
    }

    fun extractPrice(priceStr: String) : Int{
        val regex = "\\d+".toRegex()

        val matchResult = regex.find(priceStr)
        return matchResult?.value?.toInt() ?: 0
    }
}


