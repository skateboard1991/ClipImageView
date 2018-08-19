# ClipImageView
用法
```
    fun startCropImage(context: Activity, requestCode: Int, inputPath: String, outputPath: String, clipWidth: Int, clipHeight: Int, maxScale: Float = 1.0f, minScale: Float = 1.0f)
```

其中requestCode是请求码，最后在onActivityResult中得到返回的Uri，其中inputPath和outputPath分别代表输入图片路径和输出图片路径，clipWidth和clipHeight代表裁剪框
的宽高




