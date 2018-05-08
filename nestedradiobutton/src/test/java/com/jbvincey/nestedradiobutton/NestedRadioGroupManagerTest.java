package com.jbvincey.nestedradiobutton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jbvincey on 03/04/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class NestedRadioGroupManagerTest {

    private NestedRadioGroupManager nestedRadioGroupManager;

    @Before
    public void setup() {
        nestedRadioGroupManager = new NestedRadioGroupManager();
        nestedRadioGroupManager = spy(nestedRadioGroupManager);
    }

    @Test
    public void initCheckedId() {
        //given
        int initialId = 12345;

        //when
        nestedRadioGroupManager.initCheckedId(initialId);

        //then
        assertEquals(initialId, nestedRadioGroupManager.checkedId);
        assertEquals(initialId, nestedRadioGroupManager.initialCheckedId);
    }

    @Test
    public void addNestedRadioButton() {
        //given
        int radioButtonId = 12345;
        NestedRadioButton nestedRadioButton = mock(NestedRadioButton.class);
        when(nestedRadioButton.getId()).thenReturn(radioButtonId);

        //when
        nestedRadioGroupManager.addNestedRadioButton(nestedRadioButton);

        //then
        verify(nestedRadioButton).setOnCheckedChangeListener(nestedRadioGroupManager.childOnCheckedChangeListener);
    }

    @Test
    public void check() {
        //given
        int initialCheckId = 12345;
        int newCheckId = 54321;
        nestedRadioGroupManager.initCheckedId(initialCheckId);

        //when
        nestedRadioGroupManager.check(newCheckId);

        //then
        verify(nestedRadioGroupManager).setCheckedStateForView(initialCheckId, false);
        verify(nestedRadioGroupManager).setCheckedStateForView(newCheckId, true);
        verify(nestedRadioGroupManager).setCheckedId(newCheckId);

    }

}