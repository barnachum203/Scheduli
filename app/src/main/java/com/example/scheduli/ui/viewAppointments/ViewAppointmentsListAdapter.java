package com.example.scheduli.ui.viewAppointments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scheduli.R;
import com.example.scheduli.data.Appointment;
import com.example.scheduli.data.Provider;
import com.example.scheduli.data.ProviderDataRepository;
import com.example.scheduli.data.fireBase.ProviderDataBaseCallback;
import com.example.scheduli.ui.appointmentDetails.AppointmentDetailsActivity;
import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ViewAppointmentsListAdapter extends RecyclerView.Adapter implements Filterable {
    private final LayoutInflater inflater;
    private final Context context;
    private final ProviderDataRepository providerRepository;

    private List<Appointment> appointmentList; //complete data list
    private List<Appointment> shownAppointments; // filtered list

    public Filter getTimeFilter() {
        return timeFilter;
    }

    public ViewAppointmentsListAdapter(Context context) {
        this.context = context;
        providerRepository = ProviderDataRepository.getInstance();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.apointment_view_items, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AppointmentViewHolder appointmentViewHolder = (AppointmentViewHolder) holder;

        if (shownAppointments != null && shownAppointments.size() > 0) {
            final Appointment current = shownAppointments.get(position);

            //set title for appointment
            ProviderDataRepository.getInstance().getProviderByUid(current.getProviderUid(), new ProviderDataBaseCallback() {
                @Override
                public void onCallBack(Provider provider) {
                    String titleString = context.getString(R.string.appointment_item_title, provider.getCompanyName(), current.getServiceName());

                    appointmentViewHolder.appointmentTitle.setText(titleString);
                    appointmentViewHolder.appointmentAddress.setText(provider.getAddress());
                    appointmentViewHolder.appointmentPhone.setText(provider.getPhoneNumber());
                }
            });

            //set date of appointment
            Date date = new Date(current.getStart());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            appointmentViewHolder.appointmentDate.setText(dateFormat.format(date));

            //set time of appointment
            Date start = new Date(current.getStart());
            Date end = new Date(current.getEnd());
            DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            String timeString = timeFormatter.format(start) + " - " + timeFormatter.format(end);
            appointmentViewHolder.appointmentTime.setText(timeString);
            appointmentViewHolder.currentPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if (shownAppointments != null) {
            return shownAppointments.size();
        }
        return 0;
    }

    public void setAppointmentList(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            ArrayList<Appointment> appointments = new ArrayList<>();
            Iterable<DataSnapshot> appoinmentIndex = dataSnapshot.getChildren();
            for (DataSnapshot appointmentData : appoinmentIndex) {
                String key = appointmentData.getKey();
                Appointment appointment = appointmentData.getValue(Appointment.class);
                appointments.add(appointment);
            }

            this.shownAppointments = appointments;
            this.appointmentList = new ArrayList<>(this.shownAppointments);
            Collections.sort(shownAppointments, Appointment.BY_DATETIME_DESCENDING);
            notifyDataSetChanged();
        }
    }


    @Override
    public Filter getFilter() {
        return timeFilter;
    }


    //setup filter for appointments
    private Filter timeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Appointment> filteredList = new ArrayList<>();

            if (constraint.equals("future")) {
                Date currentTime = android.icu.util.Calendar.getInstance().getTime();
                for (Appointment appointment : appointmentList) {
                    Date appointmentStartTime = new Date(appointment.getStart());
                    if (currentTime.before(appointmentStartTime))
                        filteredList.add(appointment);
                }
            } else if (constraint.equals("past")) {
                Date currentTime = android.icu.util.Calendar.getInstance().getTime();
                for (Appointment appointment : appointmentList) {
                    Date appointmentStartTime = new Date(appointment.getStart());
                    if (currentTime.after(appointmentStartTime))
                        filteredList.add(appointment);
                }
            } else {
                filteredList.addAll(appointmentList);
            }

            //end of filtering
            Collections.sort(filteredList, Appointment.BY_DATETIME_DESCENDING);
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (shownAppointments != null) {
                shownAppointments.clear();
                shownAppointments.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };


    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentTitle;
        TextView appointmentDate;
        TextView appointmentTime;
        TextView appointmentPhone;
        TextView appointmentAddress;
        int currentPosition;

        public AppointmentViewHolder(@NonNull final View itemView) {
            super(itemView);

            appointmentTitle = itemView.findViewById(R.id.tv_item_appointmentTitle);
            appointmentDate = itemView.findViewById(R.id.tv_item_appointmentDate);
            appointmentTime = itemView.findViewById(R.id.tv_item_appointmentTime);
            appointmentPhone = itemView.findViewById(R.id.tv_item_appointmentContact);
            appointmentAddress = itemView.findViewById(R.id.tv_item_appointment_address);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkIfEmpty(appointmentTitle) && !checkIfEmpty(appointmentDate) && !checkIfEmpty(appointmentTime) && !checkIfEmpty(appointmentPhone) && !checkIfEmpty(appointmentAddress)) {
                        Intent detailsIntent = new Intent(context, AppointmentDetailsActivity.class);
                        //detailsIntent.putExtra(AppointmentDetailsActivity.APPOINTMENT_DETAILS, new Appointment(appointmentList.get(currentPosition)));
                    }
                }
            });
        }


        private boolean checkIfEmpty(TextView textView) {
            return textView.getText().toString().isEmpty();
        }
    }
}
