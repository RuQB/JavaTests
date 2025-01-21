package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Flight> flightList = FlightBuilder.createFlights();

        System.out.println("Original Flights' List:");
        printFlightList(flightList);

        System.out.println("Flights' list with the exclusion flights where departure is before the given date");
        printFlightList(Filters.excludeByDeparture(flightList, LocalDateTime.now(), ComparisonStyle.LESS));

        System.out.println("Flights' list with the exclusion flights  " +
                "where the arrival date is earlier than the departure date");
        printFlightList(Filters.excludeIncorrectSegments(flightList));

        System.out.println("Flights' list with the exclusion flights " +
                "with total waiting time between layovers more than 2 hours: ");
        printFlightList(Filters.excludeIntervalsBySumming(flightList, 120, ComparisonStyle.MORE));

    }

    /**
     * Выводит в консоль список полётов в удобочитаемом формате.
     */
    private static void printFlightList(List<Flight> flightList){
        int i = 1;
        for(Flight flight : flightList){
            System.out.println(i + ". " + flight);
            i++;
        }
        System.out.println("\n");
    }
}
