package io.wurmatron.petfeeder.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    public MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    public String name;

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return Transformations.map(mIndex, new Function<Integer, String>() {
            @Override
            public String apply(Integer input) {
                return "Section: " + name;
            }
        });
    }
}