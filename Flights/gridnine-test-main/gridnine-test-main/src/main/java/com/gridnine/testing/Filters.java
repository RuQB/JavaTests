package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Filters {


    public static List<Flight> excludeByDeparture(List<Flight> flightList, LocalDateTime requiredDate,
                                           ComparisonStyle comparisonStyle) {

        List<Flight> filteredFlights = new ArrayList<>();

        for (Flight flight: flightList) {
            List <Segment> segments = flight.getSegments();
            LocalDateTime departureDate = segments.get(0).getDepartureDate();

            formatDate(requiredDate);

            switch (comparisonStyle){
                case EQUALLY:
                    if (!departureDate.isEqual(requiredDate)) {
                        filteredFlights.add(flight);
                    } break;

                case LESS:
                    if (!departureDate.isBefore(requiredDate)) {
                        filteredFlights.add(flight);
                    } break;

                case MORE:
                    if (!departureDate.isAfter(requiredDate)) {
                        filteredFlights.add(flight);
                    } break;
            }
        }

        return filteredFlights;
    }



    public static List<Flight> excludeIncorrectSegments(List<Flight> flightList){

        List<Flight> filteredFlights = new ArrayList<>();

        for (Flight flight: flightList) {

            List <Segment> segments = flight.getSegments();
            boolean excludeFlag = false;

            for (Segment segment: segments){
                if (segment.getDepartureDate().isAfter(segment.getArrivalDate())){
                    excludeFlag = true;
                    break;
                }
            }

            if (!excludeFlag) {
                filteredFlights.add(flight);
            }
        }

        return filteredFlights;
    }





    public static List<Flight> excludeIntervalsBySumming(List<Flight> flightList, int minutes, ComparisonStyle comparisonStyle){

        List<Flight> filteredFlights = new ArrayList<>();

        for (Flight flight: flightList) {
            List<Segment> segments = flight.getSegments();
            int tempInterval = 0;

            if (segments.size() > 1){

                for (int i = 0; i < segments.size() - 1; i++) {
                    tempInterval += segments.get(i).getArrivalDate().until(segments.get(i + 1).getDepartureDate(),
                            ChronoUnit.MINUTES);
                }

                switch (comparisonStyle) {
                    case EQUALLY:
                        if (!(tempInterval == minutes)) {
                            filteredFlights.add(flight);
                        }
                        break;
                    case MORE:
                        if (!(tempInterval > minutes)) {
                            filteredFlights.add(flight);
                        }
                        break;

                    case LESS:
                        if (!(tempInterval < minutes)) {
                            filteredFlights.add(flight);
                        }
                        break;
                }

            } else {
                filteredFlights.add(flight);

            }
        }

        return filteredFlights;
    }

    private static void formatDate(LocalDateTime dateTime){
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        dateTime.format(fmt);
    }

}
