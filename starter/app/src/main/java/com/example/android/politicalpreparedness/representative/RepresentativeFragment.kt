package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    private var snackbar: Snackbar? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var binding: FragmentRepresentativeBinding
    lateinit var viewModel: RepresentativeViewModel
    var notUSCountry: String? = null
    private val runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    companion object {
        //TODO: Add Constant for Location request
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33

    }

    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_representative, container, false)
        val dataSource = ElectionDatabase.getInstance(requireActivity().application).electionDao

        val viewModelFactory = ElectionsViewModelFactory(dataSource)

        viewModel =
                ViewModelProviders.of(
                        this).get(RepresentativeViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        val adapter = RepresentativeListAdapter()
//        binding.recyclerAllElections.adapter = adapter
        binding.recyclerRepresentatives.adapter = adapter
        binding.recyclerRepresentatives.layoutManager = LinearLayoutManager(requireContext())
        viewModel.representatives.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        //TODO: Establish bindings

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                hideKeyboard()
                checkDeviceLocationSettingsAndShowLocation()
            }
        }
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.searchBasedOnAddress(binding.state.selectedItem as String)
        }
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
//        if (
//                grantResults.isEmpty() ||
//                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
//        ) {
        if (
                grantResults.isEmpty() ||
                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                        PackageManager.PERMISSION_DENIED)
        ) {            // Permission denied.

            snackbar = Snackbar.make(
                    binding.representativeFragmentParent,
                    "You need to grant permission", Snackbar.LENGTH_INDEFINITE
            )
            snackbar?.setAction("Settings") {
                // Displays App settings screen.
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
            snackbar?.show()

        } else {
//            getLocation()
            checkDeviceLocationSettingsAndShowLocation()

        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            val resultCode = when {
                runningQOrLater -> {
                    // this provides the result[BACKGROUND_LOCATION_PERMISSION_INDEX]
                    permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
                }
                else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }
//            val resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE


            requestPermissions(
                    permissionsArray,
                    resultCode
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        val foregroundLocationApproved = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
        val backgroundPermissionApproved =
                if (runningQOrLater) {
                    PackageManager.PERMISSION_GRANTED ==
                            ContextCompat.checkSelfPermission(
                                    requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                } else {
                    true
                }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        try {
            if (isPermissionGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val address = geoCodeLocation(task.result!!)
                        if (notUSCountry != null) {
                            viewModel.setViewsFromAddress(address)
                            Toast.makeText(requireContext(), "You are not currently located in the united states\n You are in $notUSCountry", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.setAddress(address)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error with getting your location", Toast.LENGTH_SHORT).show()
                        Log.e("aaaaaaaa", "Exception: $task.exception")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message ?: "security exception")
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        val add: Address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Log.i("countrycountry", address.countryName)
                    Log.i("countrycode", address.countryCode)

                    if (address.countryCode.equals("US", true)) {
                        notUSCountry = null
                    } else {
                        notUSCountry = address.countryName
                    }
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode
                            ?: "000")

                }
                .first()
        return add
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun checkDeviceLocationSettingsAndShowLocation(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                            requireActivity(),
                            REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                snackbar = Snackbar.make(
                        binding.representativeFragmentParent,
                        "You must turn on location services to use the 'my location' feature", Snackbar.LENGTH_INDEFINITE
                )
                snackbar?.setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndShowLocation()
                }
                snackbar?.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {

            if (resultCode == RESULT_OK) {
                getLocation()
            } else {
                checkDeviceLocationSettingsAndShowLocation(false)

            }
        }
    }

    override fun onStop() {
        super.onStop()
        //dismisses any snackbar being shown when the fragment is no longer visible
        snackbar?.dismiss()
    }
}