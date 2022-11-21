import clock.SetableClock;
import fraction.Fraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stat.EventsStatistic;
import stat.EventsStatisticImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventsStatisticImplTest {
    private Instant start;
    private SetableClock clock;
    private EventsStatistic stat;

    @BeforeEach
    public void setUp() {
        start = Instant.now();
        clock = new SetableClock(start);
        stat = new EventsStatisticImpl(clock);
    }

    @Test
    public void simpleTest() {
        stat.incEvent("storm");
        
        for (int i = 0; i < 3; i++) {
            clock.setNow(start.plusSeconds(i * 10));
            stat.incEvent("tsunami");
        }
        
        assertEquals(stat.getEventStatisticByName("storm"), new Fraction(1, 60));
        assertEquals(stat.getEventStatisticByName("tsunami"), new Fraction(3, 60));
        assertEquals(stat.getAllEventStatistic(),
                Map.of("storm", new Fraction(1, 60),
                        "tsunami", new Fraction(3, 60)));
    }

    private void checkStorm(int num, int den) {
        assertEquals(stat.getEventStatisticByName("storm"), new Fraction(num, den));
        assertEquals(stat.getAllEventStatistic(), Map.of("storm", new Fraction(num, den)));
    }

    private void checkStormAndTsunami(int numS, int denS, int numT, int denT) {
        assertEquals(stat.getEventStatisticByName("storm"), new Fraction(numS, denS));
        assertEquals(stat.getEventStatisticByName("tsunami"), new Fraction(numT, denT));
        assertEquals(stat.getAllEventStatistic(), Map.of(
                "storm", new Fraction(numS, denS),
                "tsunami", new Fraction(numT, denT)));
    }

    @Test
    public void stormOnceAnHourTest() {
        for (int i = 0; i < 3; i++) {
            clock.setNow(start.plus(i, ChronoUnit.HOURS));
            stat.incEvent("storm");
            checkStorm(1, 60);
        }
    }

    @Test
    public void increasingStormsTest() {
        for (int i = 0; i < 5; i++) {
            clock.setNow(start.plus(i, ChronoUnit.HOURS));

            for (int j = 0; j <= i; j++) {
                stat.incEvent("storm");
            }

            checkStorm(i + 1, 60);
        }
    }

    @Test
    public void exactTimeStormsTest() {
        clock.setNow(Instant.parse("2022-11-21T19:19:59Z"));
        stat.incEvent("storm");
        checkStorm(1, 60);

        clock.setNow(Instant.parse("2022-11-21T19:20:00Z"));
        stat.incEvent("storm");
        checkStorm(1, 30);

        clock.setNow(Instant.parse("2022-11-21T19:20:01Z"));
        stat.incEvent("storm");
        checkStorm(1, 20);

        clock.setNow(Instant.parse("2022-11-21T20:19:58Z"));
        checkStorm(1, 20);

        clock.setNow(Instant.parse("2022-11-21T20:19:59Z"));
        checkStorm(1, 30);

        clock.setNow(Instant.parse("2022-11-21T20:20:00Z"));
        checkStorm(1, 60);

        clock.setNow(Instant.parse("2022-11-21T20:20:01Z"));
        checkStorm(0, 60);
    }

    @Test
    public void exactTimeStormsAndTsunamisTest() {
        clock.setNow(Instant.parse("2022-11-21T19:19:59Z"));
        stat.incEvent("storm");
        stat.incEvent("tsunami");
        checkStormAndTsunami(1, 60, 1, 60);

        clock.setNow(Instant.parse("2022-11-21T19:20:00Z"));
        stat.incEvent("storm");
        checkStormAndTsunami(1, 30, 1, 60);

        clock.setNow(Instant.parse("2022-11-21T19:20:01Z"));
        stat.incEvent("storm");
        stat.incEvent("tsunami");
        checkStormAndTsunami(1, 20, 1, 30);

        clock.setNow(Instant.parse("2022-11-21T20:19:58Z"));
        checkStormAndTsunami(1, 20, 1, 30);

        clock.setNow(Instant.parse("2022-11-21T20:19:59Z"));
        stat.incEvent("tsunami");
        checkStormAndTsunami(1, 30, 1, 30);

        clock.setNow(Instant.parse("2022-11-21T20:20:00Z"));
        stat.incEvent("storm");
        checkStormAndTsunami(1, 30, 1, 30);

        clock.setNow(Instant.parse("2022-11-21T20:20:01Z"));
        checkStormAndTsunami(1, 60, 1, 60);
    }
}
