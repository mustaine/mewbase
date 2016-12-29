package com.tesco.mewbase.bson;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by MuSTa1nE on 29/12/2016.
 */
public class BsonPathTest {

    private static final String PRODUCT_LONG = "productLong";
    private static final String PRODUCT_INT = "productInt";
    private static final String PRODUCT_SHORT = "productShort";
    private static final String PRODUCT_BYTE = "productByte";
    private static final String PRODUCT_STRING = "productString";
    private BsonObject doc;

    @Before
    public void setUp() throws Exception {
        BsonObject products = new BsonObject()
                .put(PRODUCT_INT, 2)
                .put(PRODUCT_LONG, 2L)
                .put(PRODUCT_SHORT, (short) 2)
                .put(PRODUCT_BYTE, (byte) 2)
                .put(PRODUCT_STRING, "2");
        doc = new BsonObject()
                .put("basketID", "basket")
                .put("products", products);
    }

    @Test
    public void add_int_to_Int_value() throws Exception {
        BsonObject result = BsonPath.add(doc, 1, "products", PRODUCT_INT);
        assertEquals(Integer.valueOf(2), result.getBsonObject("products").getInteger(PRODUCT_LONG));
    }

    @Test
    public void add_int_to_Long_value() throws Exception {
        BsonObject result = BsonPath.add(doc, 1, "products", PRODUCT_LONG);
        assertEquals(Long.valueOf(3), result.getBsonObject("products").getLong(PRODUCT_LONG));
    }

    @Test
    public void add_int_to_Short_value() throws Exception {
        BsonObject result = BsonPath.add(doc, 1, "products", PRODUCT_SHORT);
        assertEquals(Integer.valueOf(3), result.getBsonObject("products").getInteger(PRODUCT_SHORT));
    }

    @Test
    public void add_int_to_Byte_value() throws Exception {
        BsonObject result = BsonPath.add(doc, 1, "products", PRODUCT_BYTE);
        assertEquals(Integer.valueOf(3), result.getBsonObject("products").getInteger(PRODUCT_BYTE));
    }

    @Test
    public void add_int_to_non_existing_value() throws Exception {
        BsonObject result = BsonPath.add(doc, 1, "products", "non_existing_value");
        assertEquals(Integer.valueOf(1), result.getBsonObject("products").getInteger("non_existing_value"));
    }

    @Test
    public void add_int_to_String_throws_exception() throws Exception {
        try {
            BsonPath.add(doc, 1, "products", PRODUCT_STRING);
            fail("expected exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot increment 2", e.getMessage());
        }
    }
}