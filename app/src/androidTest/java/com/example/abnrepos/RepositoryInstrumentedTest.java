package com.example.abnrepos;

import android.content.Context;
import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.abnrepos.data.Repository;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Repository Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class RepositoryInstrumentedTest {

    Repository repo = new Repository(12, "name", "full name", "description", "visibility", "html url", false, null);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.abnrepos", appContext.getPackageName());
    }

    @Test
    public void parcel_isCorrect() {
        Parcel parcel = Parcel.obtain();
        repo.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        assertEquals(repo, Repository.CREATOR.createFromParcel(parcel));
    }
}