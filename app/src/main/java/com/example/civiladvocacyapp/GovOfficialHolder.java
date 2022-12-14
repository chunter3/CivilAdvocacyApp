package com.example.civiladvocacyapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GovOfficialHolder extends RecyclerView.ViewHolder {

    TextView officeOfOfficial;
    TextView nameOfOfficial;
    TextView partyOfOfficial;
    ImageView imageOfOfficial;


    public GovOfficialHolder(@NonNull View itemView) {
        super(itemView);
        officeOfOfficial = itemView.findViewById(R.id.mainOfficeOfOfficial);
        nameOfOfficial = itemView.findViewById(R.id.mainNameOfOfficial);
        partyOfOfficial = itemView.findViewById(R.id.mainPartyOfOfficial);
        imageOfOfficial = itemView.findViewById(R.id.mainImageOfOfficial);
    }
}
