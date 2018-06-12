package com.example.daniellecoulter.ad340_coulter;


import android.content.SharedPreferences;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SharedPreferencesTester {

    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor ;

    private SharedPreferencesHelper mMockSharedPreferencesHelper;

    private String test_String = "This tests Shared Preferences";

    @Before
    public void initMocks() {

        //Instantiate mocked SharedPreferences
        mMockSharedPreferencesHelper = createMockSharedPreference();
    }

    @Test
    public void sharedPreferences_SaveAndReadEntry() {

        // Save the entered text information to SharedPreferences
        boolean success = mMockSharedPreferencesHelper.putSharedPreferencesHelper(test_String);

        assertThat("SharedPreferenceEntry.save... returns true",
                success, is(true));

        assertEquals(test_String, mMockSharedPreferencesHelper.getSharedPreferences());

    }

    //Creates mocked SharedPreferences object to read/write
    private SharedPreferencesHelper createMockSharedPreference() {

        //Read SharedPreferences, mock if previously written correctly.
        when(mMockSharedPreferences.getString(eq("text_entry"), anyString()))
                .thenReturn(test_String);

        //Mock the successful commit
        when(mMockEditor.commit()).thenReturn(true);

        //Return the MockEditor when requested
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);

        return new SharedPreferencesHelper(mMockSharedPreferences);
        }
    }
