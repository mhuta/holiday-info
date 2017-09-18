package com.mateuszhuta.holiday.client;

import com.mateuszhuta.holiday.HolidayInfoAppConfiguration;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HolidayApiClient {
    private static final String HOLIDAYS_API_URL = "https://holidayapi.com/v1/holidays";
    private static final String KEY_PARAM = "key";
    private static final String COUNTRY_PARAM = "country";
    private static final String YEAR_PARAM = "year";
    private static final String FORMAT_PARAM = "format";
    private static final String RESPONSE_FORMAT = "csv";
    private static final int OK_HTTP_STATUS = 200;

    private final HolidayInfoAppConfiguration holidayInfoAppConfiguration;
    private final JerseyHttpClient httpClient;

    @Inject
    public HolidayApiClient(HolidayInfoAppConfiguration holidayInfoAppConfiguration, JerseyHttpClient httpClient) {
        this.holidayInfoAppConfiguration = holidayInfoAppConfiguration;
        this.httpClient = httpClient;
    }

    public String retrieveHolidayDataInCsvFormat(String country, String year) {
        Response response = httpClient.getClient().target(HOLIDAYS_API_URL)
            .queryParam(KEY_PARAM, holidayInfoAppConfiguration.getLiveApiKey())
            .queryParam(COUNTRY_PARAM, country)
            .queryParam(YEAR_PARAM, year)
            .queryParam(FORMAT_PARAM, RESPONSE_FORMAT)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

        if (response.getStatus() != OK_HTTP_STATUS) {
            throw new IllegalStateException("Something went wrong!");
        }

        return response.readEntity(String.class);
    }
}
