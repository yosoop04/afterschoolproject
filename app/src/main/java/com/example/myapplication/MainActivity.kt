package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    getDate()
    }

    fun init() {
        val nickname=findViewById<TextView>(R.id.str)
        val nickname2=findViewById<EditText>(R.id.edi)
        val btn1=findViewById<Button>(R.id.btn)
        val image=findViewById<ImageView>(R.id.face)
        val btn2=findViewById<Button>(R.id.search)

        btn1.setOnClickListener{
            nickname.text=nickname2.text.toString()

            image.setImageResource(R.drawable.ic_baseline_person_pin_24)

        }
        btn2.setOnClickListener{
            Toast.makeText(this, "hello",Toast.LENGTH_SHORT).show()
        }
    }


    fun getDate():String{
    val now=System.currentTimeMillis()
    val currentDate=Date(now)
    val dateFormat=SimpleDateFormat("yyyyMMdd")

    Toast.makeText(this,"오늘 날짜는" +dateFormat.format(currentDate).toString()+"입니다",Toast.LENGTH_SHORT).show()
    return dateFormat.format(currentDate).toString()
    }

}