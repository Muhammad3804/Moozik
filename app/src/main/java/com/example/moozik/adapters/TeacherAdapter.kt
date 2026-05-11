package com.example.moozik.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moozik.R
import com.example.moozik.models.Teacher
import com.example.moozik.util.loadAssetImage

class TeacherAdapter(
    private val onTeacherSelected: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    private val items = mutableListOf<Teacher>()

    fun submitList(teachers: List<Teacher>) {
        items.clear()
        items.addAll(teachers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher, parent, false)
        return TeacherViewHolder(view, onTeacherSelected)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TeacherViewHolder(
        itemView: View,
        private val onTeacherSelected: (Teacher) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageAvatar: ImageView = itemView.findViewById(R.id.imageTeacherAvatar)
        private val textName: TextView = itemView.findViewById(R.id.textTeacherName)
        private val textTitle: TextView = itemView.findViewById(R.id.textTeacherTitle)
        private val textRating: TextView = itemView.findViewById(R.id.textTeacherRating)
        private val textStudents: TextView = itemView.findViewById(R.id.textTeacherStudents)
        private val textExperience: TextView = itemView.findViewById(R.id.textTeacherExperience)
        private val textSpecialty: TextView = itemView.findViewById(R.id.textTeacherSpecialty)
        private val buttonBook: Button = itemView.findViewById(R.id.buttonBookTeacher)

        fun bind(teacher: Teacher) {
            if (teacher.imageAsset.isNotBlank()) {
                imageAvatar.loadAssetImage(teacher.imageAsset, R.drawable.ic_avatar)
            } else {
                imageAvatar.setImageResource(R.drawable.ic_avatar)
            }
            textName.text = teacher.name
            textTitle.text = teacher.title
            textRating.text = "Rating ${teacher.rating}  |  ${teacher.hourlyPrice}"
            textStudents.text = "Students ${teacher.students}  |  Sessions ${teacher.sessions}"
            textExperience.text = teacher.experience
            textSpecialty.text = teacher.specialty

            itemView.setOnClickListener { onTeacherSelected(teacher) }
            buttonBook.setOnClickListener { onTeacherSelected(teacher) }
        }
    }
}

