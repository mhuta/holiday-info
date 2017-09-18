package com.mateuszhuta.holiday;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;
import io.dropwizard.Configuration;

import javax.validation.Valid;

public class HolidayInfoAppConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("liveApiKey")
    private String liveApiKey;

    public String getLiveApiKey() {
        return liveApiKey;
    }
}
