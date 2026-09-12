package com.amerhwitat.cpu4096sim.mobile
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
class MainActivity : Activity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val r=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(32,32,32,32)};val t=TextView(this).apply{text="CPU4096 Simulator — Kotlin Mobile";textSize=22f;gravity=Gravity.CENTER};val s=TextView(this).apply{text="ISA inventory\nMemory/MMIO model\n128D research state: ready";textSize=16f;gravity=Gravity.CENTER;setPadding(0,24,0,24)};val b=Button(this).apply{text="Start simulation";setOnClickListener{s.text="Simulation: active\nDeterministic model: ready\n128D state: active"}};r.addView(t);r.addView(s);r.addView(b);setContentView(r)}}
