package com.example.ece651;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ece651.Model.Data;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {


    public DashBoardFragment() {
        // Required empty public constructor
    }
  //floating button addition
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //adding booleans for listener

    private boolean isOpen=false;

    //creating object for animation class
    private Animation FadOpen,FadClose;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //INcome and expense REsults
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // Inflate the layout for this fragment

        View myview = inflater.inflate(R.layout.fragment_dash_board2, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


                //connecting the above declared buttons and text to layout
        fab_main_btn= myview.findViewById(R.id.fb_main_plus_btm);
        fab_income_btn= myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_button);

        //Connecting the result textviews
        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //recycler connection

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);



        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);
        //animation connector ie loadin the newly created animation directory and resources
        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adds data to the texts present
                addData();
                if(isOpen)//floating button is open
                {
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);
                    fab_income_txt.startAnimation(FadClose);
                    fab_expense_txt.startAnimation(FadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }
                else
                {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);
                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }
        });
        //calculating the totals

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalsum=0;
                for (DataSnapshot mysnap:dataSnapshot.getChildren())
                {
                    Data data=mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();
                    String strResult=String.valueOf(totalsum);
                    totalIncomeResult.setText(strResult+".00");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalsum=0;
                for (DataSnapshot mysnap:dataSnapshot.getChildren())
                {
                    Data data=mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();
                    String strTotalsum=String.valueOf(totalsum);
                    totalExpenseResult.setText(strTotalsum+".00");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Recycler
        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);
    return myview;
    }
    //floating button animation
    private void ftAnimation()
    {
        if(isOpen)//floating button is open
        {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_txt.startAnimation(FadClose);
            fab_expense_txt.startAnimation(FadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }
        else
        {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);
            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }

    }

    //craeting onclicklistener for income and expense button


    private void addData()
    {
        //fab button income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDatainsert();

            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDatainsert();

            }
        });
    }
    public void incomeDatainsert()
    {
        AlertDialog.Builder mydialogue=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialogue.setView(myview);
        final AlertDialog dialog=mydialogue.create();
        dialog.setCancelable(false);
        final EditText editAmount=myview.findViewById(R.id.amount_edt);
        final EditText edtType=myview.findViewById(R.id.type_edt);
        final EditText edtNote=myview.findViewById(R.id.note_edt);
        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        //creating onclicklistener for above buttons

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=editAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type))
                {
                    edtType.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(amount))
                {
                    editAmount.setError("Required Field");
                    return;
                }
                int ouramountint=Integer.parseInt(amount);
                if(TextUtils.isEmpty(note))
                {
                    edtNote.setError("Required Field");
                    return;
                }
                String id=mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(ouramountint,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDatainsert()
    {
        AlertDialog.Builder mydialogue=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialogue.setView(myview);

        final AlertDialog dialog=mydialogue.create();
        dialog.setCancelable(false);
        final EditText amount=myview.findViewById(R.id.amount_edt);
        final EditText type=myview.findViewById(R.id.type_edt);
       final  EditText note=myview.findViewById(R.id.note_edt);
        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmount=amount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();
                if(TextUtils.isEmpty(tmAmount))
                {
                    amount.setError("Required Field");
                    return;
                }
                int inamount=Integer.parseInt(tmAmount);
                if(TextUtils.isEmpty(tmtype))
                {
                    type.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(tmnote)) {
                    note.setError("Required Field");
                    return;
                }
                String id=mExpenseDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added!", Toast.LENGTH_SHORT).show();

                ftAnimation();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(Data.class,R.layout.dashboard_income,DashBoardFragment.IncomeViewHolder.class,mIncomeDatabase) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, Data model, int position) {
                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmount(model.getAmount());
                viewHolder.setmIncomeDate(model.getDate());

            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(Data.class,R.layout.dashboard_expense,DashBoardFragment.ExpenseViewHolder.class,mExpenseDatabase) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, Data model, int position) {
                viewHolder.setExpenseAmount(model.getAmount());
                viewHolder.setEXpenseType(model.getType());
                viewHolder.setEXpenseDate(model.getDate());
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);

    }
    //For Income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder
    {
        View mIncomeView;
        public IncomeViewHolder(View itemView)
        {
            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeType(String type)
        {
            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }
        public void setIncomeAmount(int amount)
        {
            TextView mAmount=mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setmIncomeDate(String date)
        {
            TextView mDate=mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }


    }
    //For expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder
    {
        View myEXpenseView;
        public ExpenseViewHolder(View itemView)
        {
            super(itemView);
            myEXpenseView=itemView;
        }
        public void setEXpenseType(String type)
        {
            TextView mtype=myEXpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmount(int amount)
        {
            TextView mAmount=myEXpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setEXpenseDate(String date)
        {
            TextView mDate=myEXpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}
