package com.example.moozik.models

import java.io.Serializable

data class Teacher(
    val id: String,
    val name: String,
    val title: String,
    val instrument: String,
    val rating: Double,
    val hourlyPrice: String,
    val students: Int,
    val sessions: Int,
    val experience: String,
    val specialty: String,
    val imageAsset: String = ""
) : Serializable

