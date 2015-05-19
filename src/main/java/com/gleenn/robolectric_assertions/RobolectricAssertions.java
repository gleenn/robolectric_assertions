package com.gleenn.robolectric_assertions;

import android.support.v4.app.FragmentActivity;

import org.fest.assertions.api.ANDROID;

public class RobolectricAssertions extends ANDROID {
    public static ActivityAssert assertThat(FragmentActivity actual) {
        return new ActivityAssert(actual);
    }
}
