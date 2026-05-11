package com.example.moozik.data

import com.example.moozik.models.Teacher

object TeacherRepository {
    private val teachers = listOf(
        Teacher("t1", "Ahsan Raza", "Lead Guitar Instructor", "Guitar", 4.9, "PKR 3,000/hr", 230, 980, "9 yrs exp", "Acoustic + Electric", "teacher1.jpg"),
        Teacher("t2", "Mahnoor Fatima", "Piano Performance Coach", "Piano", 4.8, "PKR 2,800/hr", 185, 740, "7 yrs exp", "Classical + Jazz", "teacher2.jpg"),
        Teacher("t3", "Umer Siddiqui", "Drums and Rhythm Trainer", "Drums", 4.7, "PKR 2,400/hr", 160, 620, "8 yrs exp", "Rock + Fusion", "teacher3.jpg"),
        Teacher("t4", "Hassan Ali", "Bass Fundamentals Mentor", "Bass", 4.6, "PKR 2,100/hr", 120, 470, "6 yrs exp", "Groove + Timing", "teacher5.jpg"),
        Teacher("t5", "Iqra Nadeem", "Violin Technique Coach", "Violin", 4.9, "PKR 3,200/hr", 140, 530, "10 yrs exp", "Classical + Orchestra", "teacher6.jpg")
    )

    fun allTeachers(): List<Teacher> = teachers
}

