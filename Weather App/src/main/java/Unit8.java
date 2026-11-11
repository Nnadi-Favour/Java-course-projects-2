import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Unit Eight: Weather Information App
 *
 * This application uses Java Swing to display current weather
 * and a five-day forecast for a city entered by the user.
 *
 * Weather data comes from the free Open-Meteo API.
 */
public class Unit8 extends JFrame {

    private static final long serialVersionUID = 1L;

    // HTTP client used to communicate with the weather API.
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // GUI components.
    private final JTextField cityField = new JTextField("Lagos", 15);
    private final JButton searchButton = new JButton("Get Weather");

    private final JComboBox<String> temperatureUnits =
            new JComboBox<>(new String[]{"Celsius (°C)", "Fahrenheit (°F)"});

    private final JComboBox<String> windUnits =
            new JComboBox<>(new String[]{"km/h", "mph"});

    private final JLabel locationLabel = new JLabel("Search for a city");
    private final JLabel iconLabel = new JLabel("🌤️", SwingConstants.CENTER);
    private final JLabel conditionLabel =
            new JLabel("Current weather conditions will appear here");
    private final JLabel temperatureLabel = new JLabel("--");
    private final JLabel humidityLabel = new JLabel("Humidity: --");
    private final JLabel windLabel = new JLabel("Wind speed: --");
    private final JLabel statusLabel = new JLabel("Ready");

    private final JTextArea forecastArea = new JTextArea();

    private final DefaultListModel<String> historyModel =
            new DefaultListModel<>();

    private final JPanel rootPanel =
            new JPanel(new BorderLayout(12, 12));

    // Stores the last successful response so unit changes do not
    // require another API request.
    private WeatherData lastWeather;

    /**
     * Constructs the main application window.
     */
    public Unit8() {
        super("Unit 8 - Weather Information App");

        buildInterface();
        configureEvents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(760, 540));
        setLocationRelativeTo(null);
    }

    /**
     * Creates and arranges all graphical components.
     */
    private void buildInterface() {

        rootPanel.setBorder(new EmptyBorder(14, 14, 10, 14));
        rootPanel.setBackground(new Color(225, 239, 250));

        setContentPane(rootPanel);

        JLabel title = new JLabel(
                "Weather Information App",
                SwingConstants.CENTER
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
                "Current Weather and Five-Day Forecast",
                SwingConstants.CENTER
        );

        JPanel heading = new JPanel(new GridLayout(2, 1, 3, 3));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        // Search panel.
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 7, 8)
        );

        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("City:"));
        searchPanel.add(cityField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Temperature:"));
        searchPanel.add(temperatureUnits);
        searchPanel.add(new JLabel("Wind:"));
        searchPanel.add(windUnits);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        rootPanel.add(topPanel, BorderLayout.NORTH);

        // Current weather panel.
        JPanel currentPanel = new JPanel(new BorderLayout(12, 8));

        currentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Current Weather"),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        currentPanel.setBackground(new Color(245, 250, 255));

        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 58));
        locationLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        temperatureLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JPanel weatherText = new JPanel(new GridLayout(3, 1, 3, 3));
        weatherText.setOpaque(false);
        weatherText.add(locationLabel);
        weatherText.add(conditionLabel);
        weatherText.add(temperatureLabel);

        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        detailsPanel.setOpaque(false);
        detailsPanel.add(humidityLabel);
        detailsPanel.add(windLabel);

        currentPanel.add(iconLabel, BorderLayout.WEST);
        currentPanel.add(weatherText, BorderLayout.CENTER);
        currentPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Forecast display.
        forecastArea.setEditable(false);
        forecastArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        forecastArea.setText("The five-day forecast will appear here.");

        JScrollPane forecastScroll = new JScrollPane(forecastArea);
        forecastScroll.setBorder(
                BorderFactory.createTitledBorder("Five-Day Forecast")
        );

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(currentPanel, BorderLayout.NORTH);
        centerPanel.add(forecastScroll, BorderLayout.CENTER);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // Recent search history.
        JList<String> historyList = new JList<>(historyModel);
        historyList.setVisibleRowCount(7);

        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setPreferredSize(new Dimension(245, 150));
        historyScroll.setBorder(
                BorderFactory.createTitledBorder("Recent Searches")
        );

        historyList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()
                    && historyList.getSelectedValue() != null) {

                String entry = historyList.getSelectedValue();
                int separator = entry.indexOf(" — ");

                if (separator > 0) {
                    cityField.setText(entry.substring(0, separator));
                }
            }
        });

        rootPanel.add(historyScroll, BorderLayout.EAST);

        statusLabel.setBorder(new EmptyBorder(4, 2, 0, 2));
        rootPanel.add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Connects buttons and selection controls to their actions.
     */
    private void configureEvents() {

        searchButton.addActionListener(event -> searchWeather());
        cityField.addActionListener(event -> searchWeather());

        temperatureUnits.addActionListener(event -> refreshDisplayedUnits());
        windUnits.addActionListener(event -> refreshDisplayedUnits());
    }

    /**
     * Validates the city name and starts the weather request.
     */
    private void searchWeather() {

        String city = cityField.getText().trim();

        if (city.isEmpty()) {
            showError("Please enter a city name.");
            return;
        }

        searchButton.setEnabled(false);
        statusLabel.setText("Loading weather for " + city + "...");
        forecastArea.setText("Contacting the weather service. Please wait.");

        // Perform network operations in the background so the GUI
        // remains responsive while waiting for the API response.
        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {

            @Override
            protected WeatherData doInBackground() throws Exception {
                return fetchWeather(city);
            }

            @Override
            protected void done() {

                searchButton.setEnabled(true);

                try {
                    lastWeather = get();

                    displayWeather(lastWeather);
                    addToHistory(lastWeather.cityName);

                    statusLabel.setText("Weather updated successfully.");

                } catch (Exception exception) {

                    Throwable cause = exception.getCause() == null
                            ? exception
                            : exception.getCause();

                    String message = cause.getMessage();

                    if (message == null || message.isBlank()) {
                        message = "Unable to retrieve weather. "
                                + "Check your internet connection and try again.";
                    }

                    showError(message);
                    statusLabel.setText("Weather update failed.");
                }
            }
        };

        worker.execute();
    }

    /**
     * Gets the coordinates of a city and requests its weather data.
     */
    private WeatherData fetchWeather(String city) throws Exception {

        String encodedCity = URLEncoder.encode(
                city,
                StandardCharsets.UTF_8
        );

        // Convert the city name into geographical coordinates.
        String geocodingUrl =
                "https://geocoding-api.open-meteo.com/v1/search"
                + "?name=" + encodedCity
                + "&count=1&language=en&format=json";

        JSONObject geocodingResponse = getJson(geocodingUrl);
        JSONArray results = geocodingResponse.optJSONArray("results");

        // Use length() for compatibility with different JSON library versions.
        if (results == null || results.length() == 0) {
            throw new IllegalArgumentException(
                    "No matching location was found. "
                    + "Check the spelling and try again."
            );
        }

        JSONObject place = results.getJSONObject(0);

        double latitude = place.getDouble("latitude");
        double longitude = place.getDouble("longitude");

        String cityName = place.optString("name", city);
        String country = place.optString("country", "");
        String timezone = place.optString("timezone", "auto");

        // Request current conditions and five days of forecast data.
        String weatherUrl =
                "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,"
                + "is_day,weather_code,wind_speed_10m"
                + "&daily=weather_code,temperature_2m_max,"
                + "temperature_2m_min,precipitation_probability_max"
                + "&forecast_days=5"
                + "&timezone=" + URLEncoder.encode(
                        timezone,
                        StandardCharsets.UTF_8
                )
                + "&temperature_unit=celsius"
                + "&wind_speed_unit=kmh";

        JSONObject weatherResponse = getJson(weatherUrl);
        JSONObject current = weatherResponse.getJSONObject("current");
        JSONObject daily = weatherResponse.getJSONObject("daily");

        WeatherData data = new WeatherData();

        data.cityName = cityName
                + (country.isBlank() ? "" : ", " + country);

        data.temperatureC = current.getDouble("temperature_2m");
        data.humidity = current.getInt("relative_humidity_2m");
        data.windKmh = current.getDouble("wind_speed_10m");
        data.weatherCode = current.getInt("weather_code");
        data.isDay = current.optInt("is_day", 1) == 1;

        data.forecastDates = daily.getJSONArray("time");
        data.forecastCodes = daily.getJSONArray("weather_code");
        data.forecastMaxC = daily.getJSONArray("temperature_2m_max");
        data.forecastMinC = daily.getJSONArray("temperature_2m_min");
        data.precipitationChance =
                daily.getJSONArray("precipitation_probability_max");

        return data;
    }

    /**
     * Sends an HTTP GET request and converts its JSON response.
     */
    private JSONObject getJson(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(java.time.Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "The weather service returned HTTP "
                    + response.statusCode()
                    + ". Please try again later."
            );
        }

        return new JSONObject(response.body());
    }

    /**
     * Displays current conditions and forecast information.
     */
    private void displayWeather(WeatherData data) {

        boolean fahrenheit = temperatureUnits.getSelectedIndex() == 1;
        boolean milesPerHour = windUnits.getSelectedIndex() == 1;

        locationLabel.setText(data.cityName);
        conditionLabel.setText(getWeatherDescription(data.weatherCode));
        iconLabel.setText(getWeatherIcon(data.weatherCode, data.isDay));

        double temperature = fahrenheit
                ? convertToFahrenheit(data.temperatureC)
                : data.temperatureC;

        temperatureLabel.setText(
                String.format(
                        Locale.US,
                        "%.1f °%s",
                        temperature,
                        fahrenheit ? "F" : "C"
                )
        );

        humidityLabel.setText("Humidity: " + data.humidity + "%");

        double windSpeed = milesPerHour
                ? data.windKmh * 0.621371
                : data.windKmh;

        windLabel.setText(
                String.format(
                        Locale.US,
                        "Wind speed: %.1f %s",
                        windSpeed,
                        milesPerHour ? "mph" : "km/h"
                )
        );

        // Build the five-day forecast.
        StringBuilder forecast = new StringBuilder();

        forecast.append(
                String.format(
                        Locale.US,
                        "%-12s %-5s %-20s %-17s%n",
                        "Date",
                        "Icon",
                        "Conditions",
                        "Low / High"
                )
        );

        forecast.append(
                "------------------------------------------------"
                + "--------------------------\n"
        );

        for (int i = 0; i < data.forecastDates.length(); i++) {

            int code = data.forecastCodes.getInt(i);

            double lowC = data.forecastMinC.getDouble(i);
            double highC = data.forecastMaxC.getDouble(i);

            double low = fahrenheit ? convertToFahrenheit(lowC) : lowC;
            double high = fahrenheit ? convertToFahrenheit(highC) : highC;

            String rainChance = data.precipitationChance.isNull(i)
                    ? "N/A"
                    : data.precipitationChance.get(i) + "%";

            forecast.append(
                    String.format(
                            Locale.US,
                            "%-12s %-5s %-20s %4.1f / %-4.1f °%s  Rain: %s%n",
                            data.forecastDates.getString(i),
                            getWeatherIcon(code, true),
                            shorten(getWeatherDescription(code), 20),
                            low,
                            high,
                            fahrenheit ? "F" : "C",
                            rainChance
                    )
            );
        }

        forecastArea.setText(forecast.toString());
        forecastArea.setCaretPosition(0);

        updateBackground(data.isDay);
    }

    /**
     * Refreshes displayed values when the user changes units.
     */
    private void refreshDisplayedUnits() {
        if (lastWeather != null) {
            displayWeather(lastWeather);
        }
    }

    /**
     * Adds a city and timestamp to the recent-search list.
     */
    private void addToHistory(String cityName) {

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        historyModel.add(0, cityName + " — " + timestamp);

        // Keep the most recent ten searches.
        while (historyModel.size() > 10) {
            historyModel.remove(historyModel.size() - 1);
        }
    }

    /**
     * Changes the application background based on time of day.
     */
    private void updateBackground(boolean isDay) {

        int hour = LocalDateTime.now().getHour();
        Color background;

        if (!isDay || hour >= 19 || hour < 6) {
            background = new Color(39, 49, 79);       // Night
        } else if (hour >= 17) {
            background = new Color(250, 207, 173);    // Evening
        } else if (hour < 10) {
            background = new Color(218, 239, 250);    // Morning
        } else {
            background = new Color(225, 239, 250);    // Daytime
        }

        rootPanel.setBackground(background);

        statusLabel.setForeground(
                (!isDay || hour >= 19 || hour < 6)
                        ? Color.WHITE
                        : new Color(25, 54, 86)
        );

        rootPanel.repaint();
    }

    /**
     * Displays an error dialog to the user.
     */
    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Weather App Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Converts Celsius to Fahrenheit.
     */
    private static double convertToFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    /**
     * Converts Open-Meteo weather codes into readable descriptions.
     */
    private static String getWeatherDescription(int code) {

        if (code == 0) return "Clear sky";
        if (code == 1) return "Mainly clear";
        if (code == 2) return "Partly cloudy";
        if (code == 3) return "Overcast";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77) return "Snow";
        if (code >= 80 && code <= 82) return "Rain showers";
        if (code == 85 || code == 86) return "Snow showers";
        if (code >= 95) return "Thunderstorm";

        return "Weather conditions unavailable";
    }

    /**
     * Returns an emoji icon representing the weather condition.
     */
    private static String getWeatherIcon(int code, boolean isDay) {

        if (code == 0) return isDay ? "☀️" : "🌙";
        if (code == 1) return isDay ? "🌤️" : "☁️";
        if (code == 2) return "⛅";
        if (code == 3) return "☁️";
        if (code == 45 || code == 48) return "🌫️";
        if (code >= 51 && code <= 67) return "🌧️";
        if (code >= 71 && code <= 77) return "❄️";
        if (code >= 80 && code <= 82) return "🌦️";
        if (code == 85 || code == 86) return "🌨️";
        if (code >= 95) return "⛈️";

        return "🌡️";
    }

    /**
     * Shortens long forecast labels so the forecast stays readable.
     */
    private static String shorten(String text, int limit) {

        if (text.length() <= limit) {
            return text;
        }

        return text.substring(0, limit - 3) + "...";
    }

    /**
     * Holds current weather and forecast data returned by the API.
     */
    private static class WeatherData {

        String cityName;

        double temperatureC;
        int humidity;
        double windKmh;

        int weatherCode;
        boolean isDay;

        JSONArray forecastDates;
        JSONArray forecastCodes;
        JSONArray forecastMaxC;
        JSONArray forecastMinC;
        JSONArray precipitationChance;
    }

    /**
     * Starts the GUI on Swing's Event Dispatch Thread.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Unit8 application = new Unit8();
            application.setVisible(true);
        });
    }
}