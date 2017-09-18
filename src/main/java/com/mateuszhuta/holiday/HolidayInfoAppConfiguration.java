package com.mateuszhuta.holiday;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class HolidayInfoAppConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("liveApiKey")
    private String liveApiKey;

    public String getLiveApiKey() {
        return liveApiKey;
    }
}
