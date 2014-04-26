package com.sabdroidex.test;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by marc on 23/03/14.
 */
public class ConversionTest extends AndroidTestCase {

    public void testNumberFormat() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        Number number = format.parse("256");

        if (!(number.longValue() < Integer.MIN_VALUE || number.longValue() > Integer.MAX_VALUE)) {
            number = Integer.valueOf(number.intValue());
        }

        Assert.assertTrue(number instanceof Integer);

        format = NumberFormat.getIntegerInstance();
        number = format.parse("2404191237");

        if (!(number.longValue() < Integer.MIN_VALUE || number.longValue() > Integer.MAX_VALUE)) {
            number = Integer.valueOf(number.intValue());
        }

        Assert.assertTrue(number instanceof Long);
    }

}
