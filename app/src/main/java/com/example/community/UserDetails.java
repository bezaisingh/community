package com.example.community;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UserDetails extends AppCompatActivity implements
        View.OnClickListener {

    private EditText firstName, middleName, lastName,Address,State,Pin,District, Country,MobNumber,upiId;
    String country, dist, pin, state, addr, DOB, mName, fName, lName, strGender,MobileNumber, emailId, strUPIid;
    private TextView EmailId, navProfileName,dob;
    private Button btnNext,btnSkip;
    private FirebaseAuth firebaseAuth;
    private RadioButton mGenderOptions;
    private RadioGroup mGender;
    private FirebaseDatabase firebaseDatabase;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private ImageView navProfilImage;
    private FirebaseStorage firebaseStorage;
    DatabaseReference userRef;

    private int mYear, mMonth, mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();

        userRef= FirebaseDatabase.getInstance().getReference("User Profiles").child(firebaseAuth.getUid());

        firstName=findViewById(R.id.fName);
        middleName=findViewById(R.id.mName);
        lastName=findViewById(R.id.lName);
        Address=findViewById(R.id.etaddress);
        State=findViewById(R.id.state);
        Pin= findViewById(R.id.Pin);
        District=findViewById(R.id.dist);
        Country=findViewById(R.id.country);
        dob=findViewById(R.id.DOB);
        btnNext=findViewById(R.id.btnSave);
        btnSkip=findViewById(R.id.btnSkip);
        MobNumber=findViewById(R.id.mobNumber);
        EmailId=findViewById(R.id.tvEmailId);
        upiId=findViewById(R.id.upi_Id);

        dob.setOnClickListener(this);


        mGender=findViewById(R.id.rgGender);

        //Gender ka coding//

        mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mGenderOptions = mGender.findViewById(checkedId);
                switch (checkedId){

                    case R.id.radioMale:
                        strGender=mGenderOptions.getText().toString();
                        break;

                    case R.id.radioFemale:
                        strGender=mGenderOptions.getText().toString();
                        break;

                        default:

                }
            }
        });


//Displaying the data from database//

        firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =firebaseDatabase.getReference("User Profiles").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);

                firstName.setText(userProfile.getFname());
                middleName.setText(userProfile.getMname());
                lastName.setText(userProfile.getLname());
                dob.setText(userProfile.getDOB());
                District.setText(userProfile.getDistrict());
                Address.setText(userProfile.getAddress());
                State.setText(userProfile.getState());
                Pin.setText(userProfile.getPin());
                Country.setText(userProfile.getCountry());
                MobNumber.setText(userProfile.getMobileNumber());
                EmailId.setText(userProfile.getEmailId());
                upiId.setText(userProfile.getUPIid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserDetails.this, databaseError.getCode(),Toast.LENGTH_SHORT).show();

            }
        });





        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDetails.this, ChangePassword.class));
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    firebaseAuth.getCurrentUser();
                    sendUserData();
                    Toast.makeText(UserDetails.this, "Update Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserDetails.this, KycActivity.class));

                }


            }
        });



//Navigation Bar ka coding starts//

        dl=findViewById(R.id.dl);
        abdt= new ActionBarDrawerToggle(this,dl,R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();




        final NavigationView nav_view = findViewById(R.id.nav_view);
        View naView = nav_view.inflateHeaderView(R.layout.navigation_header);

        navProfileName=naView.findViewById(R.id.navUserName);
        navProfilImage= naView.findViewById(R.id.navHeaderImage);

        StorageReference storageReference = firebaseStorage.getReference(firebaseAuth.getUid());
        storageReference.child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navProfilImage);

            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String fname= dataSnapshot.child("fname").getValue().toString();
                navProfileName.setText(fname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id=item.getItemId();

                if (id==R.id.home)
                {
                    startActivity(new Intent(UserDetails.this,Second_Activity.class));
                    Toast.makeText(UserDetails.this,"Home",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.myProfile)
                {
                    startActivity(new Intent(UserDetails.this,myProfileActivity.class));
                    Toast.makeText(UserDetails.this,"My Profile",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.about)
                {
                    Toast.makeText(UserDetails.this,"Settings",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.myPosts){
                    startActivity(new Intent(UserDetails.this,myPosts.class));

                    Toast.makeText(UserDetails.this,"My Posts",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.menu_logout){

                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(UserDetails.this, MainActivity.class));
                    Toast.makeText(UserDetails.this,"Logout Success",Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(UserDetails.this,"Share with...",Toast.LENGTH_SHORT).show();


                }
                return true;
            }
        });



    }


    private Boolean validate(){
        Boolean result = false;

        fName =firstName.getText().toString();
        mName= middleName.getText().toString();
        lName = lastName.getText().toString();
        DOB = dob.getText().toString();
        addr = Address.getText().toString();
        state = State.getText().toString();
        pin = Pin.getText().toString();
        dist = District.getText().toString();
        country = Country.getText().toString();
        MobileNumber = MobNumber.getText().toString().trim();
        emailId= EmailId.getText().toString().trim();
        strUPIid=upiId.getText().toString().trim();



        if(fName.isEmpty() || lName.isEmpty() || DOB.isEmpty() || addr.isEmpty() || state.isEmpty() || pin.isEmpty() || dist.isEmpty() || country.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }

    private void sendUserData(){

        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("User Profiles").child(firebaseAuth.getUid());
        UserProfile userProfile = new UserProfile(fName,mName,lName,DOB,strGender,MobileNumber,emailId,addr,state,pin,dist,country,strUPIid);
        databaseReference.setValue(userProfile);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == dob) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
