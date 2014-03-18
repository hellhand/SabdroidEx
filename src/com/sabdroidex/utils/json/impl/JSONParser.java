package com.sabdroidex.utils.json.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marc on 14/12/13.
 */
public class JSONParser {

    private static final int OPEN_BRACE = '{';
    private static final int CLOSE_BRACE = '}';

    private static final int OPEN_BRACKET = '[';
    private static final int CLOSE_BRACKET = ']';

    private static final int COMMA = ',';

    private static final int FIELD = '"';

    private static final int MINUS = '-';
    private static final int PLUS = '+';
    private static final int ZERO = '0';
    private static final int ONE = '1';
    private static final int TWO = '2';
    private static final int TREE = '3';
    private static final int FOUR = '4';
    private static final int FIVE = '5';
    private static final int SIX = '6';
    private static final int SEVEN = '7';
    private static final int EIGHT = '8';
    private static final int NINE = '9';
    private static final int DECIMAL = '.';

    public Object parse(InputStream inputStream, AtomicInteger offset, JSONContext parentContext) throws IOException {

        if (inputStream == null) {
            throw new InvalidJSONFormatException("Null parameter");
        }

        if (parentContext == null) {
            parentContext = new JSONContext();
        }

        JSONContext jsonContext = new JSONContext();
        Map<String, Object> map = new HashMap<String, Object>();

        int c = -1;
        while ((c = inputStream.read()) != -1) {
            offset.incrementAndGet();
            switch (c) {
                case OPEN_BRACE:
                    parse(inputStream, offset, jsonContext);
                    break;
                case CLOSE_BRACE:
                    map.put(jsonContext.getElementName(), jsonContext.getElementValue());
                    parentContext.setElementValue(map);
                    return map;
                case OPEN_BRACKET:
                    parseArray(inputStream, offset, jsonContext);
                    break;
                case CLOSE_BRACKET:
                    map.put(jsonContext.getElementName(), jsonContext.getElementValue());
                    jsonContext.clear();
                    break;
                case FIELD:
                    parseString(inputStream, offset, jsonContext);
                    break;
                case COMMA:
                    map.put(jsonContext.getElementName(), jsonContext.getElementValue());
                    jsonContext.clear();
                    break;
                case MINUS:
                case ZERO:
                case ONE:
                case TWO:
                case TREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                case NINE:
                case DECIMAL:
                    offset.decrementAndGet();
                    parseNumber(inputStream, offset, jsonContext);
                    break;
            }
        }
        if (jsonContext.getElementName() == null) {
            return jsonContext.getElementValue();
        }
        else {
            map.put(jsonContext.getElementName(), jsonContext.getElementValue());
        }
        return map;
    }

    private void parseNumber(InputStream inputStream, AtomicInteger offset, JSONContext jsonContext) throws IOException {

        setStreamAbsolutePosition(inputStream, offset);

        int c = -1;
        while ((c = inputStream.read()) != -1) {
            offset.incrementAndGet();
            switch (c) {
                case MINUS:
                case ZERO:
                case ONE:
                case TWO:
                case TREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                case NINE:
                case DECIMAL:
                    if (jsonContext.getElementValue() == null) {
                        jsonContext.setElementValue("" + (char) c);
                    } else {
                        jsonContext.setElementValue((String) jsonContext.getElementValue() + (char) c);
                    }
                    break;
                default:
                    if (((String)jsonContext.getElementValue()).contains(".")) {
                        jsonContext.setElementValue(Double.valueOf((String) jsonContext.getElementValue()));
                    }
                    else {
                        jsonContext.setElementValue(Integer.valueOf((String) jsonContext.getElementValue()));
                    }
                    offset.decrementAndGet();
                    setStreamAbsolutePosition(inputStream, offset);
                    return;
            }
        }
    }

    private static void setStreamAbsolutePosition(InputStream inputStream, AtomicInteger offset) throws IOException {
        inputStream.reset();
        inputStream.skip(offset.get());
    }

    private void parseArray(InputStream inputStream, AtomicInteger offset, JSONContext jsonContext) throws IOException {

        List elements = new ArrayList();

        int c = -1;
        while ((c = inputStream.read()) != -1) {
            offset.incrementAndGet();
            switch (c) {
                case FIELD:
                    parseString(inputStream, offset, jsonContext);
                    break;
                case MINUS:
                case ZERO:
                case ONE:
                case TWO:
                case TREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                case NINE:
                case DECIMAL:
                    offset.decrementAndGet();
                    parseNumber(inputStream, offset, jsonContext);
                    break;
                case COMMA:
                    elements.add(jsonContext.getElementValue());
                    jsonContext.clearValue();
                    break;
                case CLOSE_BRACKET:
                    elements.add(jsonContext.getElementValue());
                    jsonContext.setElementValue(elements);
                    return;
            }
        }
    }

    private void parseString(InputStream inputStream, AtomicInteger offset, JSONContext jsonContext) throws IOException {

        StringWriter stringWriter = new StringWriter();
        
        int c = -1;
        while ((c = inputStream.read()) != -1) {
            offset.incrementAndGet();
            switch (c) {
                case FIELD:
                    if (jsonContext.getElementName() == null) {
                        jsonContext.setElementName(stringWriter.toString());
                    }
                    else {
                        jsonContext.setElementValue(stringWriter.toString());
                    }
                    return;
                default:
                    stringWriter.append((char) c);
            }
        }
    }
}
