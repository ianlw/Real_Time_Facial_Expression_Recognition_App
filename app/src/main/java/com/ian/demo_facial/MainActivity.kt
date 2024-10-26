package com.ian.demo_facial

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ian.demo_facial.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ian.demo_facial.ClassifyTf

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var cameraJhr: CameraJhr
    lateinit var classifyTf: ClassifyTf
    companion object {
        const val INPUT_SIZE = 48

    }
    //val classes = arrayOf("1","2","3","4","5","6","7")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classifyTf = ClassifyTf(this)

        //init cameraJHR
        cameraJhr = CameraJhr(this)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted() && !cameraJhr.ifStartCamera){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    /**
     * start Camera Jhr
     */
    private fun startCameraJhr() {
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    classifyImage(bitmap)
                }
            }
        })

        cameraJhr.initBitmap()
        cameraJhr.initImageProxy()
        //selector camera LENS_FACING_FRONT = 0;    LENS_FACING_BACK = 1;
        //aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1;  false returImageProxy, true return bitmap
        cameraJhr.start(0,0,binding.cameraPreview,true,false,true)
    }

    private fun classifyImage(bitmap: Bitmap) {
        // Redimensionar el bitmap a 48x48
        val bitmapScale = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        classifyTf.listenerIterpreter(object :ReturnInterpreter{
            override fun classify(confidence: FloatArray, maxconfidence: Int) {
                runOnUiThread {
                    binding.predictedClassTextView.text = "Clase 1: ${confidence[0]}\n" +
                            "Clase 2: ${confidence[1]}\n" +
                            "Clase 3: ${confidence[2]}\n" +
                            "Clase 4: ${confidence[3]}\n" +"Clase 5: ${confidence[4]}\n" +
                            "Clase 6: ${confidence[5]}\n" +"Clase 7: ${confidence[6]}\n" +
                            "Clase predicha:${maxconfidence+1}"
                    //binding.imgBitMap.setImageBitmap(bitmapScale)

                }
            }
            //classifyTf.classify(bitmapScale)
        })

        classifyTf.classify(bitmapScale)

            runOnUiThread {
                binding.imgBitMap.setImageBitmap(bitmapScale)
                //binding.predictedClassTextView.text = "Predicción: $predictedClass"
            }
        }
}



    /**
     * @return bitmap rotate degrees
     */
    //fun Bitmap.rotate(degrees:Float) = Bitmap.createBitmap(this,0,0,width,height,
  //      Matrix().apply { postRotate(degrees) },true)



