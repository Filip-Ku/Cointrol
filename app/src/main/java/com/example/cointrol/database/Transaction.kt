package com.example.cointrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "desc") val desc: String?,
    @ColumnInfo(name = "amount") val amount: Double?,
    @ColumnInfo(name = "date") val date: String?
)