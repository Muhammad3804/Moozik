package com.example.moozik

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moozik.util.loadAssetImage
import com.example.moozik.adapters.TeacherAdapter
import com.example.moozik.data.TeacherRepository
import com.example.moozik.models.Teacher

class TeacherFragment : BaseScreenFragment(R.layout.fragment_teacher_list) {

    private lateinit var teacherAdapter: TeacherAdapter
    private val allTeachers = TeacherRepository.allTeachers()
    private var selectedInstrument: String = "All"
    private var searchQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.teacherToolbar)
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        teacherAdapter = TeacherAdapter { teacher ->
            navigateTo(TeacherDetailFragment.newInstance(teacher), addToBackStack = true)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerTeachers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = teacherAdapter

        val chipMap = mapOf(
            "All" to R.id.chipAll,
            "Guitar" to R.id.chipGuitar,
            "Piano" to R.id.chipPiano,
            "Drums" to R.id.chipDrums,
            "Bass" to R.id.chipBass,
            "Violin" to R.id.chipViolin
        )

        chipMap.forEach { (instrument, viewId) ->
            view.findViewById<TextView>(viewId).setOnClickListener {
                selectedInstrument = instrument
                applyChipSelection(view, chipMap)
                applyFilter()
            }
        }

        view.findViewById<EditText>(R.id.editTeacherSearch).doAfterTextChanged { editable ->
            searchQuery = editable?.toString()?.trim().orEmpty()
            applyFilter()
        }

        applyChipSelection(view, chipMap)
        applyFilter()

        bindBottomNav(
            root = view,
            selectedIndex = 1,
            onStore = {
                navigateTo(
                    StoreFragment.newInstance(
                        requireActivity().intent.getStringExtra(MainActivity.EXTRA_USER_NAME)
                            ?: "Guest User"
                    )
                )
            },
            onLessons = { navigateTo(LessonsFragment()) },
            onCart = { navigateTo(CartFragment()) },
            onProfile = { navigateTo(ProfileFragment()) }
        )
    }

    private fun applyFilter() {
        val filtered = allTeachers.filter { teacher ->
            val matchesInstrument = selectedInstrument == "All" || teacher.instrument == selectedInstrument
            val haystack = "${teacher.name} ${teacher.title} ${teacher.instrument} ${teacher.specialty}".lowercase()
            val matchesSearch = searchQuery.isBlank() || haystack.contains(searchQuery.lowercase())
            matchesInstrument && matchesSearch
        }
        teacherAdapter.submitList(filtered)
    }

    private fun applyChipSelection(root: View, chipMap: Map<String, Int>) {
        chipMap.forEach { (instrument, viewId) ->
            val chip = root.findViewById<TextView>(viewId)
            if (instrument == selectedInstrument) {
                chip.setBackgroundResource(R.drawable.bg_filter_chip_selected)
                chip.setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                chip.setBackgroundResource(R.drawable.bg_filter_chip_unselected)
                chip.setTextColor(resources.getColor(R.color.colorAccent, null))
            }
        }
    }
}

class TeacherDetailFragment : BaseScreenFragment(R.layout.fragment_teacher_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.teacherDetailToolbar)
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        val teacher = readTeacherFromArgs() ?: return

        view.findViewById<android.widget.ImageView>(R.id.imageTeacherAvatar)
            .loadAssetImage(teacher.imageAsset, R.drawable.ic_avatar)
        view.findViewById<TextView>(R.id.textDetailName).text = teacher.name
        view.findViewById<TextView>(R.id.textDetailTitle).text = teacher.title
        view.findViewById<TextView>(R.id.textDetailInstrument).text = "Instrument: ${teacher.instrument}"
        view.findViewById<TextView>(R.id.textDetailRating).text = "Rating: ${teacher.rating}"
        view.findViewById<TextView>(R.id.textDetailPrice).text = "Price: ${teacher.hourlyPrice}"
        view.findViewById<TextView>(R.id.textDetailStudents).text = "Students: ${teacher.students}"
        view.findViewById<TextView>(R.id.textDetailSessions).text = "Sessions: ${teacher.sessions}"
        view.findViewById<TextView>(R.id.textDetailExperience).text = "Experience: ${teacher.experience}"
        view.findViewById<TextView>(R.id.textDetailSpecialty).text = "Specialty: ${teacher.specialty}"
    }

    private fun readTeacherFromArgs(): Teacher? {
        val args = arguments ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getSerializable(ARG_TEACHER, Teacher::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getSerializable(ARG_TEACHER) as? Teacher
        }
    }

    companion object {
        private const val ARG_TEACHER = "arg_teacher"

        fun newInstance(teacher: Teacher): TeacherDetailFragment {
            return TeacherDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TEACHER, teacher)
                }
            }
        }
    }
}



