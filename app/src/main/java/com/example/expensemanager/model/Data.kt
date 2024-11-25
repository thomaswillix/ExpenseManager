package com.example.expensemanager.model


class Data (var amount : Int, var type : String, var note : String, var id : String, var date : String) {
    override fun toString(): String {
        return "Amount: " + amount.toString() + "€, note: " + note + ", date: " + date
    }
}