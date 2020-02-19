package com.example.ece651;


import android.app.AlertDialog;
import android.icu.util.ValueIterator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ece651.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeoutException;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends Fragment {


    public IncomeFragment() {
        // Required empty public constructor
    }

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //adding recycler view
    private RecyclerView recyclerView;

    //textview

    private TextView incomeTotalSum;

    //update edit text
    private EditText editamount;
    private EditText editType;
    private EditText editNote;

    //button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_income, container, false);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        incomeTotalSum=myview.findViewById(R.id.income_txt_result);
        //call declared recycler view
        recyclerView=myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//as we have declared inside cardview
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        //trigger when a new data is added to income database
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalvalue=0;
                for(DataSnapshot mysnapshot:dataSnapshot.getChildren())
                {
                    Data data=mysnapshot.getValue(Data.class);
                    totalvalue+=data.getAmount();
                    String stTotalvalue=String.valueOf(totalvalue);
                    incomeTotalSum.setText(stTotalvalue);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myview;
    }
 //the firebase adaptor is ale=ways set and called iside onstart fuction of any java class
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(Data.class,R.layout.income_recycler_data,MyViewHolder.class,mIncomeDatabase) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();
                        post_key=getRef(position).getKey();
                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;

        }
        private void setType(String type)
        {
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setNote(String note)
        {
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private void setDate(String date)
        {
            TextView mDate=mView.findViewById(R.id.date_txt_income);//name got from incomerecycler xml
            mDate.setText(date);
        }
        private void setAmount(Integer amount)
        {
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);//name got from incomerecycler xml
            String stramount=String.valueOf(amount);
            mAmount.setText(stramount);
        }
    }
    private void updateDataItem()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);
        editamount=myview.findViewById(R.id.amount_edt);
        editNote=myview.findViewById(R.id.note_edt);
        editType=myview.findViewById(R.id.type_edt);
        //set data to edit text(to make changes to already added content
        editType.setText(type);
        editType.setSelection(type.length());
        editNote.setText(note);
        editNote.setSelection(note.length());
        editamount.setText(amount);
        editNote.setSelection(String.valueOf(amount).length());
        btnUpdate=myview.findViewById(R.id.btnUpdate);
        btnDelete=myview.findViewById(R.id.btnDelete);
        final AlertDialog dialog=mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener()
        { @Override
        public void onClick(View v) {
            dialog.dismiss();

        }

        });
        dialog.show();
    }
}
