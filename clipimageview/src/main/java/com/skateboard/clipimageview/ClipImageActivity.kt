package com.skateboard.clipimageview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_clipimage_activity.*
import java.io.File

class ClipImageActivity : AppCompatActivity()
{
    private var inputPath = ""

    private var outputPath = ""

    private var maxScale = 1.0f

    private var minScale = 1.0f

    private var clipWidth = 90

    private var clipHeight = 90

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipimage_activity)
        setSupportActionBar(toolbar)
        handleIntentData()
        initClipImageView()
        decodeImage()
    }

    private fun handleIntentData()
    {
        val bundle = intent.extras
        bundle?.let {
            inputPath = it.getString(INPUT_PATH)
            outputPath = it.getString(OUTPUT_PATH)
            maxScale = it.getFloat(MAX_SCALE)
            minScale = it.getFloat(MIN_SCALE)
            clipWidth = it.getInt(CLIP_WIDTH)
            clipHeight = it.getInt(CLIP_HEIGHT)
        }
    }

    private fun initClipImageView()
    {
        clipImageView.clipWidth = clipWidth
        clipImageView.clipHeight = clipHeight
        clipImageView.minScale = minScale
        clipImageView.maxScale = maxScale
        clipImageView.onsaveClipImageListener = object : ClipImageView.OnSaveClipImageListsner
        {
            override fun onImageFinishedSav()
            {
                hideLoadingProgress()
                val intent = Intent()
                intent.data = Uri.fromFile(File(outputPath))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }
    }

    private val loadImageTask = object : AsyncTask<Void, Void, Bitmap>()
    {
        override fun onPreExecute()
        {
            super.onPreExecute()
            showLoadingProgress()
        }

        override fun doInBackground(vararg params: Void?): Bitmap
        {
            return BitmapFactory.decodeFile(inputPath)
        }

        override fun onPostExecute(result: Bitmap?)
        {
            super.onPostExecute(result)
            hideLoadingProgress()
            clipImageView.setImageBitmap(result)
        }
    }

    private fun decodeImage()
    {
        loadImageTask.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.clip_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == R.id.use)
        {
            showLoadingProgress()
            clipImageView.clipAndSaveImage(outputPath)
        }
        return false
    }

    private fun showLoadingProgress()
    {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingProgress()
    {
        progressBar.visibility = View.GONE
    }

    companion object
    {
        val CLIP_WIDTH = "clip_width"
        val CLIP_HEIGHT = "clip_height"
        val MAX_SCALE = "max_scale"
        val MIN_SCALE = "min_scale"
        val INPUT_PATH = "input_path"
        val OUTPUT_PATH = "output_path"

        fun startCropImage(context: Activity, requestCode:Int,inputPath: String, outputPath: String, clipWidth: Int, clipHeight: Int, maxScale: Float = 1.0f, minScale: Float = 1.0f)
        {
            val intent = Intent(context, ClipImageActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(CLIP_WIDTH, clipWidth)
            bundle.putInt(CLIP_HEIGHT, clipHeight)
            bundle.putFloat(MAX_SCALE, maxScale)
            bundle.putFloat(MIN_SCALE, minScale)
            bundle.putString(INPUT_PATH, inputPath)
            bundle.putString(OUTPUT_PATH, outputPath)
            intent.putExtras(bundle)
            context.startActivityForResult(intent,requestCode)
        }
    }
}