package com.mateuszhuta.holiday.api;

import java.util.List;

public class HolidayInfo {
    private final String date;
    private final List<String> names;

    public HolidayInfo(String date, List<String> names) {
        this.date = date;
        this.names = names;
    }

    public String getDate() {
        return date;
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HolidayInfo)) return false;

        HolidayInfo that = (HolidayInfo) o;

        return date.equals(that.date) && names.equals(that.names);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + names.hashCode();
        return result;
    }
}
