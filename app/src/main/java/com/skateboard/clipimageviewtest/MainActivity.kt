package com.skateboard.clipimageviewtest

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.skateboard.clipimageview.ClipImageActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity()
{

    private lateinit var filePath:File

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        filePath = File(Environment.getExternalStorageDirectory().absolutePath, "pic.jpg")
        if (!filePath.exists())
        {
            filePath.createNewFile()
        }
        clipBtn.setOnClickListener {

            choiceFromAlbum()
        }
    }

    private fun choiceFromAlbum()
    {
        val choiceFromAlbumIntent = Intent(Intent.ACTION_GET_CONTENT)
        choiceFromAlbumIntent.type = "image/*"
        startActivityForResult(choiceFromAlbumIntent, 1002)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==1002)
            {
                data?.let {
                    
                    ClipImageActivity.startCropImage(this, 1001, it.dataString, filePath.absolutePath, (160 * resources.displayMetrics.density).toInt(), (160 * resources.displayMetrics.density).toInt(), 2.0f, 0.5f)
                }
            }
            else
            {
                data?.let {
                    showIV.setImageURI(it.data)
                }
            }
        }

    }
}
