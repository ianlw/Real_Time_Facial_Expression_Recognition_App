package com.ian.demo_facial

import android.content.Context
import android.graphics.Bitmap
import com.ian.demo_facial.ml.ModeloMlp
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClassifyTf(context: Context) {

    private val model = ModeloMlp.newInstance(context)
    lateinit var returnInterpreter: ReturnInterpreter
    fun listenerIterpreter(returnInterpreter: ReturnInterpreter){
        this.returnInterpreter = returnInterpreter
    }

    fun classify(bitmap: Bitmap) {
        // Crear el buffer de entrada para el modelo
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 2304), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(1 * 2304 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Procesar el bitmap en escala de grises y normalizar los valores
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val grayValue = (0.299 * ((pixel shr 16) and 0xff) +
                        0.587 * ((pixel shr 8) and 0xff) +
                        0.114 * (pixel and 0xff)).toFloat()
                byteBuffer.putFloat(grayValue / 255.0f)
            }
        }

        inputFeature.loadBuffer(byteBuffer)

        // Realizar la inferencia del modelo y obtener el resultado
        val outputs = model.process(inputFeature)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidence = outputFeature0.floatArray
        val maxconfidence = confidence.indices.maxByOrNull { confidence[it] }?:0
        // Llamar al callback con el resultado del modelo
        //callback(outputFeature0.floatArray)
        returnInterpreter.classify(confidence, maxconfidence)
        // Liberar recursos del modelo
        //model.close()
    }
}
