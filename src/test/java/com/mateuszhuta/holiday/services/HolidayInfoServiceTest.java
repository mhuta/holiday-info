package com.mateuszhuta.holiday.services;

import com.mateuszhuta.holiday.http.HolidayApiClient;
import com.mateuszhuta.holiday.models.HolidayInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HolidayInfoServiceTest {
    private static final String CSV_HEADER = "name,date,observed,public\n";
    private static final String CSV_DELIMITER = ",";
    private static final String IMPROPER_CSV_HEADER = "date,name,observed,public\n";

    @Mock
    private HolidayApiClient holidayApiClient;

    private HolidayInfoService holidayInfoService;

    @Before
    public void setUp() {
        when(holidayApiClient.retrieveHolidayDataInCsvFormat(anyString(), anyString())).thenReturn(CSV_HEADER);
        holidayInfoService = new HolidayInfoService(holidayApiClient);
    }

    @Test
    public void When_LookingForCommonHolidayInTwoCountriesForSpecificDate_Expect_HolidayWasFound() {
        final String plHoliday = "Swieto Trzech Kroli";
        final String usHoliday = "Epiphany";
        final String commonDate = "2015-01-06";
        final HolidayInfo expected = new HolidayInfo(commonDate, Arrays.asList(plHoliday, usHoliday));

        when(holidayApiClient.retrieveHolidayDataInCsvFormat("PL", "2015")).thenReturn(CSV_HEADER + plHoliday + CSV_DELIMITER + commonDate);
        when(holidayApiClient.retrieveHolidayDataInCsvFormat("US", "2015")).thenReturn(CSV_HEADER + usHoliday + CSV_DELIMITER + commonDate);

        Optional<HolidayInfo> returned = holidayInfoService.findNextCommonHoliday("2015-01-01", "PL", "US");
        assertEquals(expected, returned.get());
    }

    @Test
    public void When_LookingForCommonHolidayInTwoCountriesForSpecificDate_While_NoSuchHolidayOccurs_Expect_NoHolidayWasFound() {
        final String plHoliday = "Swieto Trzech Kroli";
        final String usHoliday = "New Year's Day";
        final String plDate = "2015-01-06";
        final String usDate = "2015-01-01";

        when(holidayApiClient.retrieveHolidayDataInCsvFormat("PL", "2015")).thenReturn(CSV_HEADER + plHoliday + CSV_DELIMITER + plDate);
        when(holidayApiClient.retrieveHolidayDataInCsvFormat("US", "2015")).thenReturn(CSV_HEADER + usHoliday + CSV_DELIMITER + usDate);

        Optional<HolidayInfo> returned = holidayInfoService.findNextCommonHoliday("2015-01-01", "PL", "US");
        assertTrue(!returned.isPresent());
    }

    @Test
    public void When_LookingForCommonHolidayInTwoCountriesForSpecificDate_While_DateIsAboveLimitation_Expect_NoHolidayWasFound() {
        final String plHoliday = "Swieto Trzech Kroli";
        final String usHoliday = "Epiphany";
        final String commonDate = "2015-01-06";

        when(holidayApiClient.retrieveHolidayDataInCsvFormat("PL", "2015")).thenReturn(CSV_HEADER + plHoliday + CSV_DELIMITER + commonDate);
        when(holidayApiClient.retrieveHolidayDataInCsvFormat("US", "2015")).thenReturn(CSV_HEADER + usHoliday + CSV_DELIMITER + commonDate);

        Optional<HolidayInfo> returned = holidayInfoService.findNextCommonHoliday("2017-01-01", "PL", "US");
        assertTrue(!returned.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void When_LookingForCommonHolidayInTwoCountriesForSpecificDate_And_GotDataInImproperFormat_Expect_IllegalArgumentException() {
        final String plHoliday = "Swieto Trzech Kroli";
        final String usHoliday = "Epiphany";
        final String commonDate = "2015-01-06";

        when(holidayApiClient.retrieveHolidayDataInCsvFormat("PL", "2015")).thenReturn(IMPROPER_CSV_HEADER + commonDate + CSV_DELIMITER + plHoliday);
        when(holidayApiClient.retrieveHolidayDataInCsvFormat("US", "2015")).thenReturn(IMPROPER_CSV_HEADER + commonDate + CSV_DELIMITER + usHoliday);

        holidayInfoService.findNextCommonHoliday("2015-01-01", "PL", "US");
    }
}