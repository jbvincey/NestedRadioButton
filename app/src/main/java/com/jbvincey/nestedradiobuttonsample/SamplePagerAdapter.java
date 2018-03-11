package com.jbvincey.nestedradiobuttonsample;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jean-baptistevincey on 07/02/2018.
 */

public class SamplePagerAdapter extends PagerAdapter {


    private Context context;

    public SamplePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        SamplePagerEnum customPagerEnum = SamplePagerEnum.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayout(), collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return SamplePagerEnum.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SamplePagerEnum customPagerEnum = SamplePagerEnum.values()[position];
        return context.getString(customPagerEnum.getTitle());
    }

    private enum SamplePagerEnum {

        NESTED_SAMPLE_LINEAR(R.layout.view_nested_sample_linear, R.string.nested_sample_linear_title),
        NESTED_SAMPLE_RELATIVE(R.layout.view_nested_sample_relative, R.string.nested_sample_relative_title),
        NESTED_SAMPLE_CONSTRAINT(R.layout.view_nested_sample_constraint, R.string.nested_sample_constraint_title),
        NESTED_SAMPLE_FRAME(R.layout.view_nested_sample_frame, R.string.nested_sample_frame_title);

        private @LayoutRes int layout;

        private @StringRes int title;

        SamplePagerEnum(@LayoutRes int layout, @StringRes int title) {
            this.layout = layout;
            this.title = title;
        }

        public int getLayout() {
            return layout;
        }

        public int getTitle() {
            return title;
        }
    }

}
