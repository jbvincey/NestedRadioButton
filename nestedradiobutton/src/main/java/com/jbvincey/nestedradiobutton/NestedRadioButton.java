package com.jbvincey.nestedradiobutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jean-baptistevincey on 09/01/2018.
 */

public class NestedRadioButton extends AppCompatRadioButton {

    private @IdRes int clickableParentIdRes = View.NO_ID;

    public NestedRadioButton(@NonNull Context context) {
        super(context);
    }

    public NestedRadioButton(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public NestedRadioButton(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(@NonNull Context context, @NonNull AttributeSet attrs) {
        // retrieve selected radio button as requested by the user in the
        // XML layout file
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NestedRadioButton);

        clickableParentIdRes = attributes.getResourceId(R.styleable.NestedRadioButton_clickableParent, View.NO_ID);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int id = getId();
        // generates an id if it's missing
        if (id == View.NO_ID) {
            id = View.generateViewId();
            setId(id);
        }

        attachToParentNestedRadioGroup((View) getParent());
        if(clickableParentIdRes != View.NO_ID) {
            attachClickableParent((View) getParent());
        }
    }

    private void attachClickableParent(View view) {
        if(view != null) {
            if(view.getId() == clickableParentIdRes) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NestedRadioButton.this.setChecked(true);
                    }
                });
            } else if(view.getParent() instanceof View) {
                attachClickableParent((View) view.getParent());
            }
        }
    }

    private void attachToParentNestedRadioGroup(View view) {
        if(view == null) {
            throw new ClassCastException("NestedRadioButton should be under a NestedRadioGroup");
        } else if(view instanceof NestedRadioGroupInterface) {
            ((NestedRadioGroupInterface) view).addNestedRadioButton(this);
        } else if(view.getParent() instanceof View){
            attachToParentNestedRadioGroup((View) view.getParent());
        } else {
            throw new ClassCastException("NestedRadioButton should be under a NestedRadioGroup");
        }
    }


}
