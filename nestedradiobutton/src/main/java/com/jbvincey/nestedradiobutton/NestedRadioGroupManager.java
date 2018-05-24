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
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.VisibleForTesting;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStructure;
import android.widget.CompoundButton;

/**
 * Created by jean-baptistevincey on 11/01/2018.
 *
 * Handle nested radio group logic (see {@link NestedConstraintRadioGroup}, {@link NestedFrameRadioGroup},
 * {@link NestedLinearRadioGroup}, {@link NestedRelativeRadioGroup}.
 */

public class NestedRadioGroupManager {

    private static final String TAG = "NestedRadioGroupManager";

    // holds the checked id; the selection is empty by default
    @VisibleForTesting
    protected int checkedId = -1;
    // tracks children radio buttons checked state
    @VisibleForTesting
    protected CompoundButton.OnCheckedChangeListener childOnCheckedChangeListener;
    // when true, onCheckedChangeListener discards events
    private boolean protectFromCheckedChange = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    //private NestedLinearRadioGroup.PassThroughHierarchyChangeListener mPassThroughListener;

    // Indicates whether the child was set from resources or dynamically, so it can be used
    // to sanitize autofill requests.
    @VisibleForTesting
    protected int initialCheckedId = View.NO_ID;

    private final SparseArray<NestedRadioButton> radioButtons;


    public NestedRadioGroupManager() {
        radioButtons = new SparseArray<>();
        childOnCheckedChangeListener = new CheckedStateTracker();
    }

    public void initCheckedId(int value) {
        checkedId = value;
        initialCheckedId = value;
    }

    public int getCheckedId() {
        return checkedId;
    }

    public void addNestedRadioButton(NestedRadioButton nestedRadioButton) {
        radioButtons.put(nestedRadioButton.getId(), nestedRadioButton);
        if (checkedId == nestedRadioButton.getId()) {
            protectFromCheckedChange = true;
            setCheckedStateForView(checkedId, true);
            protectFromCheckedChange = false;
            setCheckedId(nestedRadioButton.getId());
        }
        nestedRadioButton.setOnCheckedChangeListener(childOnCheckedChangeListener);
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #clearCheck()
     */
    public void check(@IdRes int id) {
        // don't even bother
        if (id != -1 && (id == checkedId)) {
            return;
        }

        if (checkedId != -1) {
            setCheckedStateForView(checkedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    @VisibleForTesting
    protected void setCheckedId(@IdRes int id) {
        checkedId = id;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checkedId);
        }
        //TODO
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final AutofillManager afm = mContext.getSystemService(AutofillManager.class);
            if (afm != null) {
                afm.notifyValueChanged(this);
            }
        }*/
    }

    @VisibleForTesting
    protected void setCheckedStateForView(int viewId, boolean checked) {
        NestedRadioButton checkedView = findViewById(viewId);
        if (checkedView != null) {
            checkedView.setChecked(checked);
        }
    }

    private NestedRadioButton findViewById(int viewId) {
        return radioButtons.get(viewId);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void onProvideAutofillStructure(ViewStructure structure) {
        structure.setDataIsSensitive(checkedId != initialCheckedId);
    }

    public void clearCheck() {
        check(-1);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param groupManager the group in which the checked radio button has changed
         * @param checkedId    the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(NestedRadioGroupManager groupManager, @IdRes int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (protectFromCheckedChange) {
                return;
            }
            int id = buttonView.getId();

            protectFromCheckedChange = true;
            if (checkedId != -1 && checkedId != id) {
                setCheckedStateForView(checkedId, false);
            }
            protectFromCheckedChange = false;

            setCheckedId(id);
        }
    }

}
