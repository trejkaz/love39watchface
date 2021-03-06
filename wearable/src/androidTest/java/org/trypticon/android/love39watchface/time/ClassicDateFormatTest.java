/*
 * Copyright © 2016-2018 Trejkaz <trejkaz@trypticon.org>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING.WTFPL file for more details.
 */

package org.trypticon.android.love39watchface.time;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ClassicDateFormat}.
 */
public class ClassicDateFormatTest {

    @Test
    public void testAbbreviated() {
        ClassicDateFormat dateFormat = new ClassicDateFormat(LocaleTestUtils.AUSTRALIA);
        ClassicTime time = new ClassicTime();
        time.setTo(new ClassicTime(2016, 1, 27, 0, 0, 0));
        assertThat(dateFormat.formatDate(time), is(equalTo("Wed., 27 Jan.")));
    }

    @Test
    public void testNotAbbreviated() {
        ClassicDateFormat dateFormat = new ClassicDateFormat(LocaleTestUtils.AUSTRALIA);
        ClassicTime time = new ClassicTime();
        time.setTo(new ClassicTime(2016, 5, 9, 0, 0, 0));
        assertThat(dateFormat.formatDate(time), is(equalTo("Mon., 9 May")));
    }
}
