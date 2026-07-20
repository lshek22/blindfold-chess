package com.example.blindfoldchess.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.databinding.ItemBoardThemeBinding

data class BoardTheme(
    val id: String,
    val displayName: String,
    val thumbnailResId: Int
)

class BoardThemeAdapter(
    private val themes: List<BoardTheme>,
    private var selectedThemeId: String,
    private val onThemeSelected: (BoardTheme) -> Unit
) : RecyclerView.Adapter<BoardThemeAdapter.ThemeViewHolder>() {

    inner class ThemeViewHolder(val binding: ItemBoardThemeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val binding = ItemBoardThemeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themes[position]
        holder.binding.txtThemeName.text = theme.displayName
        holder.binding.imgThumbnail.setImageResource(theme.thumbnailResId)

        val isSelected = theme.id == selectedThemeId
        holder.binding.imgThumbnail.alpha = if (isSelected) 1.0f else 0.5f

        holder.itemView.setOnClickListener {
            selectedThemeId = theme.id
            notifyDataSetChanged()
            onThemeSelected(theme)
        }
    }

    override fun getItemCount(): Int = themes.size
}