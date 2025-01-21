package com.gridnine.testing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import static com.gridnine.testing.Main.printFlightList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlightFilterTest {

    final LocalDateTime dateTime = LocalDateTime.parse("2025-01-01T00:00");

    @Test
    public void excludeByDeparture() {

        List<Flight> flights = Arrays.asList(
                // #0 A normal flight
                createFlight(dateTime, dateTime.plusHours(2)),
                // #1 A flight with a departure date in the past
                createFlight(dateTime.minusMinutes(1), dateTime.plusHours(2)),
                // #2 A flight with a future departure date
                createFlight(dateTime.plusMinutes(1), dateTime.plusHours(2)),
                // #3 A normal multi segment flight
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #4 A multi segment flight with a departure date in the past
                createFlight(dateTime.minusMinutes(1), dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #5 A multi segment flight with a future departure date
                createFlight(dateTime.plusMinutes(1), dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)));

        List<Flight> moreCaseActual = Filters.excludeByDeparture(flights, dateTime, ComparisonStyle.MORE);
        List<Flight> lessCaseActual = Filters.excludeByDeparture(flights, dateTime, ComparisonStyle.LESS);
        List<Flight> equallyCaseActual = Filters.excludeByDeparture(flights, dateTime, ComparisonStyle.EQUALLY);

        List<Flight> moreCaseExpected = CreateReferenceArray(flights, new int[]{0, 1, 3, 4});
        List<Flight> lessCaseExpected = CreateReferenceArray(flights, new int[]{0, 2, 3, 5});
        List<Flight> equallyCaseExpected = CreateReferenceArray(flights, new int[]{1, 2, 4, 5});

        assertEquals(moreCaseExpected, moreCaseActual);
        assertEquals(lessCaseExpected, lessCaseActual);
        assertEquals(equallyCaseExpected, equallyCaseActual);
    }

    @Test
    public void excludeIncorrectSegments() {

        List<Flight> flights = Arrays.asList(
                // #0 A normal flight
                createFlight(dateTime, dateTime.plusHours(2)),
                // #1 A incorrect flight
                createFlight(dateTime, dateTime.minusHours(2)),
                // #2 A normal multi segment flight
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #3 A multi segment flight with incorrect first segment
                createFlight(dateTime, dateTime.minusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #4 A multi segment flight with incorrect middle segment
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(1),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #5 A multi segment flight with incorrect last segment
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(5)));

        List<Flight> correctFlightsActual = Filters.excludeIncorrectSegments(flights);
        List<Flight> correctFlightsExpected = CreateReferenceArray(flights, new int[]{0, 2});

        assertEquals(correctFlightsExpected, correctFlightsActual);
    }

    @Test
    public void excludeIntervalBySumming() {

        List<Flight> flights = Arrays.asList(
                // #0 A normal flight
                createFlight(dateTime, dateTime.plusHours(2)),
                // #1 A multi segment flight with summary ground interval less than 120 minutes
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(4).plusMinutes(30), dateTime.plusHours(7)),
                // #2 A multi segment flight with summary ground interval equally 120 minutes
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(5), dateTime.plusHours(7)),
                // #3  multi segment flight with summary ground interval more than 120 minutes
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(3), dateTime.plusHours(4),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #4  multi segment flight with 120 minutes ground interval between 1st and 2nd segments
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(4), dateTime.plusHours(5),
                        dateTime.plusHours(5), dateTime.plusHours(7)),
                // #5  multi segment flight with 120 minutes ground interval between 2nd and 3rd segments
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(2), dateTime.plusHours(3),
                        dateTime.plusHours(5), dateTime.plusHours(7)),
                // #6  multi segment flight with ground interval more than 120 minutes between 1st and 2nd segments
                createFlight(dateTime, dateTime.plusHours(1),
                        dateTime.plusHours(4), dateTime.plusHours(5),
                        dateTime.plusHours(5), dateTime.plusHours(7)),
                // #7  multi segment flight with ground interval more than 120 minutes between 2nd and 3rd segments
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(2), dateTime.plusHours(3),
                        dateTime.plusHours(6), dateTime.plusHours(7)),
                // #8  multi segment flight with ground interval less than 120 minutes between 1st and 2nd segments
                createFlight(dateTime, dateTime.plusHours(1),
                        dateTime.plusHours(2), dateTime.plusHours(5),
                        dateTime.plusHours(5), dateTime.plusHours(7)),
                // #9  multi segment flight with ground interval less than 120 minutes between 2nd and 3rd segments
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(2), dateTime.plusHours(3),
                        dateTime.plusHours(4), dateTime.plusHours(7)),
                //#10 Second multi segment flight with ground interval more than 120 minutes between 2nd and 3rd segments
                createFlight(dateTime, dateTime.plusHours(2),
                        dateTime.plusHours(2), dateTime.plusHours(3),
                        dateTime.plusHours(10), dateTime.plusHours(11)));


        List<Flight> moreCaseActual = Filters.excludeIntervalsBySumming(flights, 120, ComparisonStyle.MORE);

        List<Flight> lessCaseActual = Filters.excludeIntervalsBySumming(flights, 120, ComparisonStyle.LESS);
        List<Flight> equallyCaseActual = Filters.excludeIntervalsBySumming(flights, 120, ComparisonStyle.EQUALLY);

        List<Flight> moreCaseExpected = CreateReferenceArray(flights, new int[]{0, 1, 2, 4, 5, 8, 9});
        List<Flight> lessCaseExpected = CreateReferenceArray(flights, new int[]{0, 2, 3, 4, 5, 6, 7, 10});
        List<Flight> equallyCaseExpected = CreateReferenceArray(flights, new int[]{0, 1, 3, 6, 7, 8, 9, 10});


        assertEquals(moreCaseExpected, moreCaseActual);
        assertEquals(lessCaseExpected, lessCaseActual);

        assertEquals(equallyCaseExpected, equallyCaseActual);
    }

    /**
     * Создает экземпляр класса Flight.
     */
    private Flight createFlight(final LocalDateTime... dates) {

        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException(
                    "you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }


    private List<Flight> CreateReferenceArray(List<Flight> flights, int[] indices) {
        List<Flight> referenceArr = new ArrayList<>();
        for (int index : indices) {
            referenceArr.add(flights.get(index));
        }
        return referenceArr;
    }

    private static void printFlightList(List<Flight> flightList) {
        int i = 1;
        for (Flight flight : flightList) {
            System.out.println(i + ". " + flight);
            i++;
        }
        System.out.println("\n");
    }
}
