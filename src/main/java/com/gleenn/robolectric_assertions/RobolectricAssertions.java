package com.gleenn.robolectric_assertions;

import android.app.Activity;

public class RobolectricAssertions {
    public static ActivityAssert assertThat(Activity actual) {
        return new ActivityAssert(actual);
    }
}
