package io.wurmatron.petfeeder.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.wurmatron.petfeeder.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    public static final int[] TAB_TITLES = new int[]{R.string.tab_general, R.string.tab_schedule, R.string.tab_history, R.string.tab_settings};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
           return GeneralFragment.newInstance(position+1);
        } else if(position == 1) {
            return ScheduleFragment.newInstance(position+1);
        }  else if(position == 2) {
            return HistoryFragment.newInstance(position+1);
        } else if(position == 3) {
            return SettingsFragment.newInstance(position+1);
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}