package com.mateuszhuta.holiday.controllers;

import com.google.common.base.Strings;
import com.mateuszhuta.holiday.models.HolidayInfo;
import com.mateuszhuta.holiday.services.HolidayInfoService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/holidays")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HolidayInfoController {
    private static final String DATE_PARAM = "date";
    private static final String COUNTRY_ONE_PARAM = "country_one";
    private static final String COUNTRY_TWO_PARAM = "country_two";

    private final HolidayInfoService holidayInfoService;

    @Inject
    public HolidayInfoController(HolidayInfoService holidayInfoService) {
        this.holidayInfoService = holidayInfoService;
    }

    @GET
    public Optional<HolidayInfo> getHolidayInfo(@QueryParam(DATE_PARAM) String date,
            @QueryParam(COUNTRY_ONE_PARAM) String countryOne, @QueryParam(COUNTRY_TWO_PARAM) String countryTwo) {
        if (Strings.isNullOrEmpty(countryOne) || Strings.isNullOrEmpty(countryTwo) || countryOne.equals(countryTwo)) {
            throw new IllegalArgumentException("At least one query parameter has invalid value!");
        }

        return holidayInfoService.findNextCommonHoliday(date, countryOne, countryTwo);
    }
}