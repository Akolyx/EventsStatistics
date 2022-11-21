package stat;

import fraction.Fraction;

import java.util.Map;

public interface EventsStatistic {
    void incEvent(String name);

    Fraction getEventStatisticByName(String name);

    Map<String, Fraction> getAllEventStatistic();

    void printStatistic();
}
