package com.hassam.travellingbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SearchFragment extends Fragment {
    private ImageView settings;
    private FirebaseAuth mAuth;
    private ImageView sign_out;
    private ImageView btnExplore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ( (HomeActivity) getActivity()).changetitle("Search");

        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        //Settings Button
//        View btnSetting = view.findViewById(R.id.btn_Settings);
//        btnSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),SettingsActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//        //Sign Out Button
//        View view1 = view.findViewById(R.id.logout_btn);
//        view1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getContext(),LoginActivity.class);
//                startActivity(intent);
//                getActivity().finish();
//
//            }
//        });
//
//
//        View view2 = view.findViewById(R.id.btn_users);
//        view2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),UsersActivity.class);
//                startActivity(intent);
//            }
//        });
//
        return view;
    }
}
