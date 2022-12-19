package com.tranzsilica.qrgen

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var permissionList= arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
    var cameraPermissionGranted = false
    var storagePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "QR Gen"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissionList, 8888)
        }

        cardViewOne.setOnClickListener {
            if(storagePermissionGranted){
                startActivity(Intent(this, QrGeneratorActivity::class.java))
            } else{
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }

        }

        cardViewTwo.setOnClickListener {
            if(cameraPermissionGranted){
                startActivity(Intent(this, QrScannerActivity::class.java))
            }else{
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 8888){
            cameraPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            storagePermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED
        }
    }
}