package com.example.googlemapsample

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapsample.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    binding.searchButton.setOnClickListener {
        val location = binding.searchEditText.text.toString()

        if(location.isNotEmpty()){
            searchLocation(location)
        }
    }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        // Add a marker in Sydney and move the camera
     /*   val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

    }

    private fun getCurrentLocation() {
        mMap.isMyLocationEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener(this) { task->

                    if (task.isSuccessful && task.result!=null){
                        val lastKnownLocation:Location = task.result
                        val currentLatLng = LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15f))
                    }

        }
    }

    private fun searchLocation(location: String){
        val geocoder = Geocoder(this)
        try {
            val address = geocoder.getFromLocationName(location,1)
            if (address!=null){
                if (address.isNotEmpty()) {
                    val address = address?.get(0)
                    val latLng = address?.let {
                        LatLng(it.latitude,it.longitude)
                    }
                    latLng?.let {
                        MarkerOptions().position(it).title(location)
                    }?.let {
                        mMap.addMarker(it)
                    }
                    latLng?.let {
                        CameraUpdateFactory.newLatLngZoom(it,15f)
                    }?.let {
                        mMap.animateCamera(it)
                    }
                }
                else{
                        Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==1){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )!=PackageManager.PERMISSION_GRANTED){
                    return
                }
            }
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        }

    }


}