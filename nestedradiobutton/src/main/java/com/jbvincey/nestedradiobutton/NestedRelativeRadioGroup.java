/*
 * Copyright 2018 Jean-Baptiste VINCEY.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jbvincey.nestedradiobutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.autofill.AutofillValue;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * Created by jean-baptistevincey on 11/01/2018.
 *
 * <p>This class is used to create a multiple-exclusion scope for a set of radio
 * buttons. Checking one radio button that belongs to a radio group unchecks
 * any previously checked radio button within the same group.</p>
 * <p>
 * <p>Intially, all of the radio buttons are unchecked. While it is not possible
 * to uncheck a particular radio button, the radio group can be cleared to
 * remove the checked state.</p>
 * <p>
 * <p>The selection is identified by the unique id of the radio button as defined
 * in the XML layout file.</p>
 * <p>
 *
 * The difference with RadioGroup is that NestedRadioGroup allows to have any number of ViewGroup
 * intermediates between your NestedRadioButton and NestedRadioGroup.
 *
 * @see NestedRadioButton
 */

public class NestedRelativeRadioGroup extends RelativeLayout implements NestedRadioGroupInterface {
    private static final String LOG_TAG = NestedRelativeRadioGroup.class.getSimpleName();

    @NonNull
    private NestedRadioGroupManager nestedRadioGroupManager;

    public NestedRelativeRadioGroup(@NonNull Context context) {
        super(context);
        init();
    }

    public NestedRelativeRadioGroup(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    public NestedRelativeRadioGroup(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);
    }

    private void init() {
        nestedRadioGroupManager = new NestedRadioGroupManager();
    }

    private void initAttrs(@NonNull Context context, @NonNull AttributeSet attrs) {
        // RadioGroup is important by default, unless app developer overrode attribute.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
        }

        // retrieve selected radio button as requested by the user in the
        // XML layout file
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NestedRadioGroup, R.attr.radioButtonStyle, 0);

        int value = attributes.getResourceId(R.styleable.NestedRadioGroup_checkedButton, View.NO_ID);
        if (value != View.NO_ID) {
            nestedRadioGroupManager.initCheckedId(value);
        }

        attributes.recycle();
    }

    @Override
    public void addNestedRadioButton(NestedRadioButton nestedRadioButton) {
        nestedRadioGroupManager.addNestedRadioButton(nestedRadioButton);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(NestedRadioGroupManager.OnCheckedChangeListener listener) {
        nestedRadioGroupManager.setOnCheckedChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NestedRelativeRadioGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new NestedRelativeRadioGroup.LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof NestedRelativeRadioGroup.LayoutParams;
    }

    @Override
    protected NestedRelativeRadioGroup.LayoutParams generateDefaultLayoutParams() {
        return new NestedRelativeRadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return RadioGroup.class.getName();
    }

    /**
     * <p>This set of layout parameters defaults the width and the height of
     * the children to {@link #WRAP_CONTENT} when they are not specified in the
     * XML file. Otherwise, this class ussed the value read from the XML file.</p>
     * <p>
     * <p>See
     * {@link com.android.internal.R.styleable#LinearLayout_Layout LinearLayout Attributes}
     * for a list of all child view attributes that this class supports.</p>
     */
    public static class LayoutParams extends RelativeLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * <p>Fixes the child's width to
         * {@link ViewGroup.LayoutParams#WRAP_CONTENT} and the child's
         * height to  {@link ViewGroup.LayoutParams#WRAP_CONTENT}
         * when not specified in the XML file.</p>
         *
         * @param a          the styled attributes set
         * @param widthAttr  the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillStructure(structure, flags);
        nestedRadioGroupManager.onProvideAutofillStructure(structure);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void autofill(AutofillValue value) {
        if (!isEnabled()) return;

        if (!value.isList()) {
            Log.w(LOG_TAG, value + " could not be autofilled into " + this);
            return;
        }

        final int index = value.getListValue();
        final View child = getChildAt(index);
        if (child == null) {
            Log.w(VIEW_LOG_TAG, "RadioGroup.autoFill(): no child with index " + index);
            return;
        }

        nestedRadioGroupManager.check(child.getId());
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public
    int getAutofillType() {
        return isEnabled() ? AUTOFILL_TYPE_LIST : AUTOFILL_TYPE_NONE;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public AutofillValue getAutofillValue() {
        if (!isEnabled()) return null;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getId() == nestedRadioGroupManager.getCheckedId()) {
                return AutofillValue.forList(i);
            }
        }
        return null;
    }
}
