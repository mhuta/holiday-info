package com.mateuszhuta.holiday.resources.services;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.mateuszhuta.holiday.client.HolidayApiClient;
import com.mateuszhuta.holiday.api.HolidayInfo;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

public class HolidayInfoService {
    private static final String NEW_LINE_SPLIT_REGEX = "\\r?\\n";
    private static final String CSV_DATA_SPLIT_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String APOSTROPHE = "\"";
    private static final String EMPTY_STRING = "";
    private static final String CSV_HEADER_VALUES = "name,date";
    private static final int YEAR_LIMIT = 2016;
    private static final int DATE_POSITION = 1;
    private static final int NAME_POSITION = 0;

    private final HolidayApiClient holidayApiClient;

    @Inject
    public HolidayInfoService(HolidayApiClient holidayApiClient) {
        this.holidayApiClient = holidayApiClient;
    }

    public Optional<HolidayInfo> findNextCommonHoliday(String date, String countryOne, String countryTwo) {
        final LocalDate requestDate = LocalDate.parse(date);
        Optional<HolidayInfo> holidayInfo = getHolidayInfo(requestDate, countryOne, countryTwo, requestDate.getYear());

        if (!holidayInfo.isPresent()) {
            return getHolidayInfo(requestDate, countryOne, countryTwo, requestDate.getYear() + 1);
        }

        return holidayInfo;
    }

    private Optional<HolidayInfo> getHolidayInfo(LocalDate requestDate, String countryOne, String countryTwo, int year) {
        if (year > YEAR_LIMIT) {
            return Optional.empty();
        }

        SetMultimap<String, String> countryOneHolidays = collectHolidays(countryOne, Integer.toString(year));
        SetMultimap<String, String> countryTwoHolidays = collectHolidays(countryTwo, Integer.toString(year));

        Set<String> commonDates = findCommonDates(countryOneHolidays, countryTwoHolidays);

        return findRequestDateHolidays(requestDate, commonDates, countryOneHolidays, countryTwoHolidays);
    }

    private SetMultimap<String, String> collectHolidays(String country, String year) {
        final String holidayData = holidayApiClient.retrieveHolidayDataInCsvFormat(country, year);
        return processHolidayCsvData(holidayData);
    }

    private SetMultimap<String, String> processHolidayCsvData(String csvData) {
        validateHolidayCsvData(csvData);
        String[] lines = csvData.split(NEW_LINE_SPLIT_REGEX);

        SetMultimap<String, String> holidayMap = MultimapBuilder.hashKeys().hashSetValues().build();

        for (int i = 1; i < lines.length; i++) {
            String[] splitLine = lines[i].split(CSV_DATA_SPLIT_REGEX);
            holidayMap.put(splitLine[DATE_POSITION], splitLine[NAME_POSITION].replaceAll(APOSTROPHE, EMPTY_STRING));
        }

        return holidayMap;
    }

    private Set<String> findCommonDates(SetMultimap<String, String> countryOneHolidays, SetMultimap<String, String> countryTwoHolidays) {
        Set<String> commonHolidays = new TreeSet<>(countryOneHolidays.keySet());
        commonHolidays.retainAll(countryTwoHolidays.keySet());
        return commonHolidays;
    }

    private Optional<HolidayInfo> findRequestDateHolidays(LocalDate requestDate, Set<String> commonDates,
            SetMultimap<String, String> countryOneHolidays, SetMultimap<String, String> countryTwoHolidays) {
        return commonDates.stream()
            .filter(date -> LocalDate.parse(date).isAfter(requestDate))
            .findFirst()
            .map(date -> {
                List<String> holidayNames = new ArrayList<>();
                holidayNames.addAll(countryOneHolidays.get(date));
                holidayNames.addAll(countryTwoHolidays.get(date));

                return new HolidayInfo(date, holidayNames);
            });
    }

    private void validateHolidayCsvData(String data) {
        if (!data.startsWith(CSV_HEADER_VALUES)) {
            throw new IllegalArgumentException("Response has invalid format!");
        }
    }
}
