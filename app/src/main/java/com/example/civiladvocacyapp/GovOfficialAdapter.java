package com.example.civiladvocacyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;



public class GovOfficialAdapter extends RecyclerView.Adapter<GovOfficialHolder> {

    private final List<GovOfficial> govOfficialList;
    private final MainActivity mainAct;


    public GovOfficialAdapter(List<GovOfficial> govOfficialLst, MainActivity ma) {
        govOfficialList = govOfficialLst;
        mainAct = ma;
    }


    @NonNull
    @Override
    public GovOfficialHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View govOfficialView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gov_official_entry, parent, false);

        govOfficialView.setOnClickListener(mainAct);

        return new GovOfficialHolder(govOfficialView);
    }

    @Override
    public void onBindViewHolder(@NonNull GovOfficialHolder holder, int position) {
        GovOfficial govOfficial = govOfficialList.get(position);

        holder.officeOfOfficial.setText(govOfficial.getOffice());
        holder.nameOfOfficial.setText(govOfficial.getName());
        holder.partyOfOfficial.setText(govOfficial.getParty());
        if (govOfficial.getPhotoURL().isEmpty()) {
            Glide.with(mainAct).load(R.drawable.missing).error(R.drawable.brokenimage).into(holder.imageOfOfficial);
        } else {
            Glide.with(mainAct).load(govOfficial.getPhotoURL()).error(R.drawable.brokenimage).into(holder.imageOfOfficial);
        }
    }

    @Override
    public int getItemCount() {
        return govOfficialList.size();
    }
}
