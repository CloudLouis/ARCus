package com.sgu.student.arcus;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sgu.student.arcus.db.Database;
import com.sgu.student.arcus.db.dao.ClassesDao;
import com.sgu.student.arcus.db.entity.ClassesEntity;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private OnFragmentInteractionListener mListener;

    public ClassPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassPage newInstance(String param1, String param2) {
        ClassPage fragment = new ClassPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_page, container, false);

        Button b = (Button) v.findViewById(R.id.add_class_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassPage.this.saveClass(view);
            }
        });

        Database database = Room.databaseBuilder(ClassPage.this.getContext(), Database.class, "mainDB").allowMainThreadQueries().build();
        ClassesEntity[] classes = database.getClassesDao().getClassesEntity();
        mRecyclerView = v.findViewById(R.id.classes_list);

        mLayoutManager = new LinearLayoutManager(ClassPage.this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<ClassesEntity> dataset = new ArrayList<>();
        for(int i = 0;i<classes.length;i++){
            dataset.add(classes[i]);
        }
        mAdapter = new ClassesListAdapter(this.getContext(),dataset);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    // Save new class
    public void saveClass(View view) {
        Database database = Room.databaseBuilder(this.getContext(), Database.class, "mainDB").allowMainThreadQueries().build();
                ClassesDao classesDao = database.getClassesDao();
                ClassesEntity classes =  new ClassesEntity();
                EditText nameField = (EditText) getView().findViewById(R.id.nameField);
                classes.setName(nameField.getText().toString());
                classesDao.insert(classes);
                database.close();
        nameField.setText("");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
