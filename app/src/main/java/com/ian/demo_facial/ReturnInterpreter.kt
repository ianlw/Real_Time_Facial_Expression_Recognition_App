package com.ian.demo_facial

interface ReturnInterpreter {
    fun classify(confidence:FloatArray, maxconfidence:Int)
}