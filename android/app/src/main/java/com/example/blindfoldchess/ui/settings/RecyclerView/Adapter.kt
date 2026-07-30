package com.example.blindfoldchess.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.blindfoldchess.R
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
        val context = holder.itemView.context
        val isSelected = theme.id == selectedThemeId

        holder.binding.txtThemeName.text = theme.displayName
        holder.binding.imgThumbnail.setImageResource(theme.thumbnailResId)

        if (isSelected) {
            holder.binding.txtThemeName.setTextColor(ContextCompat.getColor(context, R.color.green_primary))
            holder.binding.imgThumbnail.alpha = 1.0f
            // FIX: Use Float (4f) instead of Int (4)
            holder.binding.imgThumbnail.strokeWidth = 4f
        } else {
            holder.binding.txtThemeName.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.imgThumbnail.alpha = 0.5f
            // FIX: Use Float (0f) instead of Int (0)
            holder.binding.imgThumbnail.strokeWidth = 0f
        }

        holder.itemView.setOnClickListener {
            selectedThemeId = theme.id
            notifyDataSetChanged()
            onThemeSelected(theme)
        }
    }

    override fun getItemCount(): Int = themes.size
}