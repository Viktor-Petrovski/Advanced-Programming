import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if (parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);

                if (operation == 1)
                    weatherDispatcher.removeObserver(forecastDisplay);

                if (operation == 2)
                    weatherDispatcher.removeObserver(currentConditions);

                if (operation == 3)
                    weatherDispatcher.addObserver(forecastDisplay);

                if (operation == 4)
                    weatherDispatcher.addObserver(currentConditions);

            }
        }
    }

    interface IObserver {
        void update();
    }

    interface IObservable {
        void addObserver(IObserver o);

        void removeObserver(IObserver o);

        void notifyObserver();

        Measurement getState();
    }

    static class Measurement {
        final float temperature;
        final float humidity;
        final float pressure;

        Measurement(float temperature, float humidity, float pressure) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
        }
    }

    static class WeatherDispatcher implements IObservable {
        private final Collection<IObserver> observers;
        Measurement state;

        WeatherDispatcher() {
            observers = new ArrayList<>();
        }

        @Override
        public void addObserver(IObserver o) {
            if (!observers.contains(o))
                observers.add(o);
        }

        @Override
        public void removeObserver(IObserver o) {
            observers.remove(o);
        }

        @Override
        public void notifyObserver() {
            List<IObserver> sortedObservers = new ArrayList<>(observers);
            sortedObservers.sort((a, b) -> a instanceof CurrentConditionsDisplay ? -1 : 1);

            sortedObservers.forEach(IObserver::update);
            System.out.println();
        }

        @Override
        public Measurement getState() {
            return state;
        }

        void setMeasurements(float temperature, float humidity, float pressure) {
            state = new Measurement(temperature, humidity, pressure);
            notifyObserver();
        }

    }

    static class CurrentConditionsDisplay implements IObserver{
        private final IObservable observable;

        CurrentConditionsDisplay(IObservable observable) {
            this.observable = observable;
            this.observable.addObserver(this);
        }

        @Override
        public void update() {
            Measurement latest = observable.getState();

            System.out.printf(
                    "Temperature: %.1fF\nHumidity: %.1f%%\n",
                    latest.temperature, latest.humidity
            );
        }
    }

    static class ForecastDisplay implements IObserver {
        private final IObservable observable;
        private Measurement old;

        ForecastDisplay(IObservable observable) {
            this.observable = observable;
            this.observable.addObserver(this);

            old = new Measurement(.0f, .0f, .0f); // dummy
        }

        @Override
        public void update() {
            Measurement latest = observable.getState();

            String state;
            if (latest.pressure > old.pressure)
                state = "Improving";
            else if (latest.pressure == old.pressure)
                state = "Same";
            else state = "Cooler";

            old = latest;

            System.out.println("Forecast: " + state);
        }
    }
}