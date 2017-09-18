package com.mateuszhuta.holiday;

import com.mateuszhuta.holiday.client.HolidayApiClient;
import com.mateuszhuta.holiday.client.JerseyHttpClient;
import com.mateuszhuta.holiday.resources.services.HolidayInfoService;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class HolidayInfoApp extends Application<HolidayInfoAppConfiguration> {
    public static void main(String[] args) throws Exception {
        new HolidayInfoApp().run(args);
    }

    public void run(HolidayInfoAppConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().packages("com.mateuszhuta.holiday");
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(configuration).to(HolidayInfoAppConfiguration.class);
                bind(HolidayInfoService.class).to(HolidayInfoService.class);
                bind(HolidayApiClient.class).to(HolidayApiClient.class);
                bind(JerseyHttpClient.class).to(JerseyHttpClient.class);
            }
        });
    }
}
