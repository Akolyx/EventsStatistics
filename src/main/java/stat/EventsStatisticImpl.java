package stat;

import clock.Clock;
import clock.NormalClock;
import fraction.Fraction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventsStatisticImpl implements EventsStatistic {
    private final Map<String, List<Instant>> events;
    private final Clock clock;

    public EventsStatisticImpl() {
        events = new HashMap<>();
        clock = new NormalClock();
    }

    public EventsStatisticImpl(Clock clock) {
        events = new HashMap<>();
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        events.putIfAbsent(name, new ArrayList<>());
        events.get(name).add(clock.now());
    }

    @Override
    public Fraction getEventStatisticByName(String name) {
        List<Instant> times = events.getOrDefault(name, List.of());

        int inLastHour = 0;

        Instant now = clock.now();

        for (int i = times.size() - 1; i >= 0; i--) {
            if (times.get(i).plus(1, ChronoUnit.HOURS).compareTo(now) <= 0) {
                break;
            } else {
                inLastHour++;
            }
        }

        return new Fraction(inLastHour, TimeUnit.HOURS.toMinutes(1));
    }

    @Override
    public Map<String, Fraction> getAllEventStatistic() {
        Map<String, Fraction> res = new HashMap<>();

        for (Map.Entry<String, List<Instant>> entry : events.entrySet()) {
            res.put(entry.getKey(), getEventStatisticByName(entry.getKey()));
        }

        return res;
    }

    @Override
    public void printStatistic() {
        for (Map.Entry<String, List<Instant>> entry : events.entrySet()) {
            List<Instant> times = entry.getValue();

            Fraction frac = new Fraction(times.size(),
                    ChronoUnit.MINUTES.between(times.get(0), times.get(times.size() - 1)) + 1);

            System.out.println(entry.getKey() + ": rpm = " + frac.getNumerator() +
                    "/" + frac.getDenominator() + " = " + frac.asDouble());
        }
    }
}
