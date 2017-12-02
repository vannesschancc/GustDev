package com.google.firebase.quickstart.database.fragment;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.quickstart.database.ProfileActivity;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Event;
import com.google.firebase.quickstart.database.models.UtilToast;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventFragment extends Fragment implements View.OnClickListener {

    // [START define_database_reference]
    //private DatabaseReference mDatabase;
    //private DatabaseReference mProfileRef;

    // [END define_database_reference]

    Button btnDatePicker, btnTimePicker,addTagBtn;
    EditText txtDate, txtTime;
    TagContainerLayout mTagContainerLayout;
    int mYear, mMonth, mDay, mHour, mMinute;
    EditText phoneEditText, locationEditText,nicknameEditText,emaiEditText, titleEditText,tagEditText,descriptionEditText;
    FloatingActionButton saveFab;
    EventFragmentCallback eventFragmentCallback;

    List<String> tags  = new ArrayList<>();
    //String key = "";

    private EventFragmentCallback mListener;

    public EventFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_event, container, false);
        btnDatePicker= rootView.findViewById(R.id.datePickerBtn);
        btnTimePicker= rootView.findViewById(R.id.timePickerBtn);
        saveFab = rootView.findViewById(R.id.saveFab);
        saveFab.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        txtDate =  rootView.findViewById(R.id.dateEditText);
        txtTime =  rootView.findViewById(R.id.timeEditText);
        phoneEditText = rootView.findViewById(R.id.phoneEditText);
        locationEditText = rootView.findViewById(R.id.locationEditText);
        nicknameEditText = rootView.findViewById(R.id.nicknameEditText);
        emaiEditText = rootView.findViewById(R.id.emailEditText);
        titleEditText = rootView.findViewById(R.id.titleEditText);
        tagEditText = rootView.findViewById(R.id.tagEditText);
        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        mTagContainerLayout = rootView.findViewById(R.id.tagcontainerLayout);
        mTagContainerLayout.setTags(tags);

        addTagBtn = rootView.findViewById(R.id.addTagBtn);
        addTagBtn.setOnClickListener(this);

        return rootView;
    }

    public interface EventFragmentCallback {
        void sendEventToServer(Event event);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            eventFragmentCallback = (EventFragmentCallback) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Fragment1Callback");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnDatePicker){
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            txtDate.setText((monthOfYear + 1) + "-" + dayOfMonth +  "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (view == btnTimePicker) {
            final Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour,mMinute,false);
            timePickerDialog.show();
        }

        if (view == saveFab) {
            if (validateForm()) {
                passEventToActivity();
            } else {
                return;
            }
        }

        if (view == addTagBtn) {
            String newTag = tagEditText.getText().toString();
            mTagContainerLayout.addTag(newTag);
            tags.add(newTag);
        }
    }


    public Event createEvent() {
        Event event = new Event(getUid());
        event.author = nicknameEditText.getText().toString();
        event.participants = new HashMap<>();

        event.tags = new HashMap<>();
        for (String tag: tags) {
            event.tags.put(tag, true);
        }

        event.title = titleEditText.getText().toString();
        event.time = txtTime.getText().toString();
        event.date = txtDate.getText().toString();
        event.description = descriptionEditText.getText().toString();
        event.location = locationEditText.getText().toString();
        event.email = emaiEditText.getText().toString();
        event.phone = phoneEditText.getText().toString();
        return event;
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            descriptionEditText.setError("Required");
            result = false;
        } else {
            descriptionEditText.setError(null);
        }
        if (TextUtils.isEmpty(titleEditText.getText().toString())) {
            titleEditText.setError("Required");
            result = false;
        } else {
            titleEditText.setError(null);
        }

        if (TextUtils.isEmpty(txtDate.getText().toString())) {
            txtDate.setError("Required");
            result = false;
        } else {
            txtDate.setError(null);
        }

        if (TextUtils.isEmpty(txtTime.getText().toString())) {
            txtTime.setError("Required");
            result = false;
        } else {
            txtTime.setError(null);
        }

        if (TextUtils.isEmpty(nicknameEditText.getText().toString())) {
            nicknameEditText.setError("Required");
            result = false;
        } else {
            nicknameEditText.setError(null);
        }

        if (TextUtils.isEmpty(emaiEditText.getText().toString())) {
            emaiEditText.setError("Required");
            result = false;
        } else {
            emaiEditText.setError(null);
        }

        if (TextUtils.isEmpty(locationEditText.getText().toString())) {
            locationEditText.setError("Required");
            result = false;
        } else {
            locationEditText.setError(null);
        }

        if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            phoneEditText.setError("Required");
            result = false;
        } else {
            phoneEditText.setError(null);
        }

        return result;
    }

    public void passEventToActivity() {
        Event newEvent = createEvent();
        eventFragmentCallback.sendEventToServer(newEvent);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public String  getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

