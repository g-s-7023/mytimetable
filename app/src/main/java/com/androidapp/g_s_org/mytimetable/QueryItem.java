package com.androidapp.g_s_org.mytimetable;

//===
//=== pair of values (name and the corresponding value used to send query)
//===
public class QueryItem {
    private int index;
    private String mValue;
    private String mValueForQuery;

    // constructor
    public QueryItem() {
    }

    public QueryItem(String value, String valueForQuery) {
        mValue = value;
        mValueForQuery = valueForQuery;
    }

    // getter and setter
    public String getValue() {
        return mValue;
    }

    public String getValueForQuery() {
        return mValueForQuery;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void setValueForQuery(String valueForQuery) {
        mValueForQuery = valueForQuery;
    }

    // clear values
    public void clearValue() {
        mValue = "";
    }

    public void clearValueForQuery() {
        mValueForQuery = "";
    }

    // if mValue is empty, return the last word of mValueForQuery
    public String getName() {
        if (mValue != null) {
            return mValue.equals("") ? getLastWordOfQuery() : mValue;
        }
        return "";
    }

    // return the last word of mValueForQuery (split by "." and ":")
    // (expect the English name of mValue)
    public String getLastWordOfQuery() {
        if (mValueForQuery != null) {
            String[] splitByColon = mValueForQuery.split(":", 0);
            String[] splitByComma = splitByColon[splitByColon.length - 1].split("\\.", 0);
            return splitByComma.length == 0 ? "" : splitByComma[splitByComma.length - 1];
        }
        return "";
    }
}
