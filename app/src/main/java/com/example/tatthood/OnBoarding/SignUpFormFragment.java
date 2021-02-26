package com.example.tatthood.OnBoarding;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tatthood.Activity.HomeActivity;
import com.example.tatthood.Activity.StartAppActivity;
import com.example.tatthood.R;
import com.example.tatthood.ViewModel.SearchViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class SignUpFormFragment extends Fragment implements View.OnKeyListener, View.OnClickListener {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword, editTextStreetHood, editTextCity, editTextPostalCode, editTextCountry, editTextState;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView signInLink;
    private TextView tv_status_choosed;
    LinearLayout linFormLayout, lin_address, lin_address_part2;
    ImageView image_profile;
    Button btn_act_sign_up,btnNext;
    StartAppActivity getAct;
    private Geocoder geocoder;


    public SignUpFormFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up_form, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        editTextStreetHood = view.findViewById(R.id.editTextStreetHood);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextPostalCode = view.findViewById(R.id.editTextPostaleCode);
        editTextCountry = view.findViewById(R.id.editTextCountry);
        editTextState = view.findViewById(R.id.editTextState);
        lin_address_part2 = view.findViewById(R.id.lin_address_part2);
        tv_status_choosed = view.findViewById(R.id.statusChoosed);
        linFormLayout = view.findViewById(R.id.formLayout);
        lin_address = view.findViewById(R.id.lin_address);
        lin_address_part2 = view.findViewById(R.id.lin_address_part2);
        image_profile = view.findViewById(R.id.image_profile);
        signInLink = view.findViewById(R.id.log_in_link);
        btn_act_sign_up = view.findViewById(R.id.btnSignUp);

        getAct = (StartAppActivity)getActivity();
        btnNext = getAct.findViewById(R.id.btnNext);
        btnNext.setText("Start");
        btnNext.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();

        editTextPassword.setOnKeyListener(this);
        editTextUsername.setOnKeyListener(this);
        editTextEmail.setOnKeyListener(this);
        linFormLayout.setOnClickListener(this);
        image_profile.setOnClickListener(this);
        btn_act_sign_up.setOnClickListener(this);

        geocoder = new Geocoder(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchViewModel model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        model.getSelectedStatus().observe(getViewLifecycleOwner(), item -> {
            tv_status_choosed.setText(item);
            switch (item) {
                case "Hood":
                    editTextUsername.setHint("Hood name");
                    lin_address.setVisibility(View.VISIBLE);
                    lin_address_part2.setVisibility(View.VISIBLE);
                    break;

                case "Artist":
                    editTextUsername.setHint("Artist name");
                    editTextStreetHood.setHint("Hood affiliated");
                    lin_address.setVisibility(View.GONE);
                    lin_address_part2.setVisibility(View.GONE);

                    break;

                case "Tattoued":
                    editTextUsername.setHint("username");
                    editTextStreetHood.setHint("Favorite Hood ");
                    lin_address.setVisibility(View.GONE);
                    lin_address_part2.setVisibility(View.GONE);
                    break;

                case "Virgin skin":
                    editTextUsername.setHint("username");
                    editTextStreetHood.setHint("interest for tattoued ");
                    lin_address.setVisibility(View.GONE);
                    lin_address_part2.setVisibility(View.GONE);
                    break;

                case "Seller":
                    editTextUsername.setHint("Store name");
                    lin_address.setVisibility(View.VISIBLE);
                    lin_address_part2.setVisibility(View.VISIBLE);
                    break;
            }
        });

    }

    public static SignUpFormFragment getInstance(){
        return new SignUpFormFragment();
    }

    public void signUp() {
        String str_username = editTextUsername.getText().toString();
        String str_email = editTextEmail.getText().toString();
        String str_password = editTextPassword.getText().toString();
        String str_confirmPassword = editTextConfirmPassword.getText().toString();
        String str_status = tv_status_choosed.getText().toString();
        String str_streetHood = editTextStreetHood.getText().toString();
        String str_city = editTextCity.getText().toString();
        String str_postal_code = editTextPostalCode.getText().toString();
        String str_country = editTextCountry.getText().toString();
        String str_state = editTextState.getText().toString();

        if (str_username.equals("")) {
            Toast.makeText(getContext(), editTextUsername.getHint() + "is required to sign up",
                    Toast.LENGTH_SHORT).show();

        } else if (str_email.equals("")) {
            Toast.makeText(getContext(), editTextEmail.getHint() + "is required to sign up",
                    Toast.LENGTH_SHORT).show();

        } else if (str_password.equals("")) {
            Toast.makeText(getContext(), editTextPassword.getHint() + "is required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else if (!str_confirmPassword.equals(str_password)) {
            Toast.makeText(getContext(), "passwords doesn't match",
                    Toast.LENGTH_SHORT).show();
        } else if ((lin_address.getVisibility() == View.VISIBLE) && (str_streetHood.equals("") || str_city.equals("") || str_postal_code.equals("") || str_country.equals("") || str_state.equals(""))) {
            Toast.makeText(getContext(), "A complete address is required to sign up", Toast.LENGTH_SHORT).show();

        } else {
            Log.d("TAG", "All seems to be well set up: so? ");
            mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("App_users").child(userId);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userId);
                        hashMap.put("username", str_username);
                        hashMap.put("username_uppercase", str_username.toUpperCase());
                        hashMap.put("email", str_email);
                        hashMap.put("imageUrl", "");
                        //Later get value from sign up from  (spinner)
                        hashMap.put("status", str_status);
                        if (str_status.equals("Hood") || str_status.equals("Seller")) {
                            String address = str_streetHood + ", " + str_city + ", " + str_postal_code + ", " + str_state;
                            Log.d("statushood", "statushood: " + tv_status_choosed.getText().toString());
                            hashMap.put("address", address);
                            hashMap.put("city", str_city);
                            hashMap.put("country", str_country);
                            try {
                                List<Address> addresses = geocoder.getFromLocationName(address,1);
                                if (addresses.size()>0) {
                                    Address value = addresses.get(0);
                                    String lat = Double.toString(value.getLatitude());
                                    String lng = Double.toString(value.getLongitude());
                                    hashMap.put("lat", lat);
                                    hashMap.put("lng",lng);
                                    Log.i(TAG, "Coord LatLng: ");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (tv_status_choosed.getText().toString().equals("Artist") || tv_status_choosed.getText().toString().equals("Tattoued")) {
                            hashMap.put("hood", str_streetHood);
                        } else if (tv_status_choosed.getText().toString().equals("Virgin skin")) {
                            hashMap.put("int", str_streetHood);
                        }

                        mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Signing up successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signUp log", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "createUserWithEmail failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signUp();
                break;
            case R.id.editTextCountry:
                editTextCountry.setNextFocusRightId(R.id.editTextState);
                break;
            case R.id.image_profile:
            case R.id.formLayout:
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                break;
            default:
                break;
        }


    }
}

