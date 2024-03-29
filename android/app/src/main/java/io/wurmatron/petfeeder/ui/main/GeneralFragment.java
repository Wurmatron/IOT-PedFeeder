package io.wurmatron.petfeeder.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.routes.UpdateHelper;


public class GeneralFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private TextView currentWeight;
    private TextView nextSchedule;

    public static GeneralFragment newInstance(int index) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        pageViewModel.name = getResources().getString(SectionsPagerAdapter.TAB_TITLES[index - 1]);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentWeight = getView().findViewById(R.id.currentWeight);
        nextSchedule = getView().findViewById(R.id.upcomingSchedule);
        UpdateHelper.updateWeight(currentWeight);
        UpdateHelper.updateNextSchedule(nextSchedule);
    }
}