package com.kosmo.zipcock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment extends Fragment {
    private static final String TAG = "lecture";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("user_id",MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);

        Button button = (Button) rootView.findViewById(R.id.request);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);
            }
        });

        Button delivery = (Button) rootView.findViewById(R.id.delivery);

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button clean = (Button) rootView.findViewById(R.id.clean);

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button installation = (Button) rootView.findViewById(R.id.installation);

        installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button togeter = (Button) rootView.findViewById(R.id.togeter);

        togeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button bug = (Button) rootView.findViewById(R.id.bug);

        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button role = (Button) rootView.findViewById(R.id.role);

        role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button lesson = (Button) rootView.findViewById(R.id.lesson);

        lesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        Button etc = (Button) rootView.findViewById(R.id.etc);

        etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Request.class);
                intent.putExtra("member_id", member_id);
                Log.d(TAG, member_id);
                startActivity(intent);

            }
        });

        return rootView;
    }



}
