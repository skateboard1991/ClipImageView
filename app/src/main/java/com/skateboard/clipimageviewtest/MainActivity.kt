package com.skateboard.clipimageviewtest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.skateboard.clipimageview.ClipImageActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filePath = File(Environment.getExternalStorageDirectory().absolutePath, "pic.jpg")
        if (!filePath.exists())
        {
            filePath.createNewFile()
        }
        clipBtn.setOnClickListener {

            ClipImageActivity.startCropImage(this, 1001, filePath.absolutePath, filePath.absolutePath, (160 * resources.displayMetrics.density).toInt(), (160 * resources.displayMetrics.density).toInt(), 2.0f, 0.5f)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            showIV.setImageURI(it.data)
        }
    }
}
