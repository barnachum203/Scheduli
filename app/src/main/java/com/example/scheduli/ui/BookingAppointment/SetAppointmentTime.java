package com.example.scheduli.ui.BookingAppointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduli.BaseMenuActivity;
import com.example.scheduli.R;
import com.example.scheduli.data.Provider;
import com.example.scheduli.data.Service;
import com.example.scheduli.data.Sessions;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetAppointmentTime extends BaseMenuActivity {

    DatabaseReference databaseReference;
    private Toolbar mainToolbar;
    private static final String TAG_SET_APPOINTMENT_ACT = "SetAppointmentTime";


    private Service service;
    private Provider provider;
    private Map<String, ArrayList<Sessions>> dailySessions; // key is a date (day/month/year).
    private ArrayList<Sessions> sessionsArrayList;
    private RecyclerView.LayoutManager mLayout;

    private String pid;
    private int servicePosition;

    private Button next;
    private Button back;
    private TextView serviceChoosen;

    //Calendar view
    CompactCalendarView compactCalendarView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    //parent (dates) cycleview (Item)
    private RecyclerView recyclerViewSlots;
    private List<String> dates;
    private SlotAdapter slotAdapter;
    private List<Date> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment_time);

        mainToolbar = findViewById(R.id.app_main_toolbar);
        setSupportActionBar(mainToolbar);

        //Calendar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);


        //init Arrays
        dates = new ArrayList<>();
        slots = new ArrayList<>();
        sessionsArrayList = new ArrayList<>();

        //Init buttons and texts
        serviceChoosen = (TextView) findViewById(R.id.set_appointment_time_act_chosen_service);
        next = (Button) findViewById(R.id.btn_next_2);
        back = (Button) findViewById(R.id.btn_back_2);

        //Get Data from previous intent
        Intent intent = getIntent();
        provider = intent.getParcelableExtra("provider");
        service = intent.getParcelableExtra("service");
        pid = intent.getStringExtra("pid");
        servicePosition = intent.getIntExtra("position", 0);
        Log.d(TAG_SET_APPOINTMENT_ACT,"Got service: " + service.getName());
        Log.d(TAG_SET_APPOINTMENT_ACT, "Got service sessoins " + "[" + service.getDailySessions().size() + "] :" + service.getDailySessions());
        serviceChoosen.setText(provider.getCompanyName() + " ⌘ " + service.getName());
        dailySessions = service.getDailySessions();

        //Get dates
        getDatesFromMap(dailySessions);

        //Get Events for calendar:
        for(int i=0; i<dates.size();i++) {
            getSessions(dates.get(i));
            Log.d(TAG_SET_APPOINTMENT_ACT,"Getting Evet data from date: " + dates.get(i) );
        }


        recyclerViewSlots = (RecyclerView) findViewById(R.id.recycleview_available_slots);
        mLayout = new GridLayoutManager(this, 3);
        slotAdapter = new SlotAdapter(slots);
        recyclerViewSlots.setLayoutManager(mLayout);
        recyclerViewSlots.setHasFixedSize(true);
        recyclerViewSlots.setAdapter(slotAdapter);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                slots.clear();

//                System.out.println("TEST: picked date: " + dateClicked.getTime());

                for (int i = 0; i < sessionsArrayList.size(); i++) {
                    Date date = new Date(sessionsArrayList.get(i).getStart());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    if(dateFormat.format(dateClicked.getTime()).compareTo(dateFormat.format(date.getTime()))==0){
                        slots.add(new Date(sessionsArrayList.get(i).getStart()));
                    }
                }

                recyclerViewSlots.setAdapter(slotAdapter);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormat.format(firstDayOfNewMonth));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Toast.makeText(getApplicationContext(),"To be continued..." ,Toast.LENGTH_SHORT).show();
                }



        });



    }


    public void getDatesFromMap(Map map) {

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
//                System.out.println(pair.getKey() + " : " + pair.getValue()); //TEST
                dates.add(pair.getKey().toString());
                it.remove();
            }
        } else {
            Log.d(TAG_SET_APPOINTMENT_ACT, "getDatesFromMap: null map");
        }
    }

    public void getSessions(String index) {
        int i = 0;
        databaseReference = FirebaseDatabase.getInstance().getReference("providers").child(pid).child("services").child(String.valueOf(servicePosition)).child("dailySessions").child(index);
        databaseReference.child(index);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            slots.clear();

            if (dataSnapshot.exists()) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Sessions sessions = snapshot.getValue(Sessions.class);
                    System.out.println("[TEST] sesions start: " + sessions.getStart()); //TEST
                    if(sessions.isAvailable()) {
                        sessionsArrayList.add(sessions);
                        slots.add(new Date(sessions.getStart()));

                        Event event = new Event(Color.GREEN, sessions.getStart(), service.getName() + " slot");
                        compactCalendarView.addEvent(event);
                    }

                }


                if (!slots.isEmpty()) {
//                    System.out.println(" [TEST] slots is not empty");
//                    recyclerViewSlots.setAdapter(slotAdapter);
                }
            } else {
                Log.d(TAG_SET_APPOINTMENT_ACT, "Snapshot is not exists");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            Log.d(TAG_SET_APPOINTMENT_ACT, "onCancellelled: Something went wrong..");
        }
    };


}
