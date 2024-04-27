import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WeatherForecastApp {

    private static JFrame frame;
    private static JPanel homePanel;
    private static JPanel weatherPanel;
    private static JTextField locationField;
    private static JTextArea weatherDisplay;
    private static JButton fetchButton;
    private static String apiKey = "0db86faf7989224decdd4501eb0daeec"; // Replace with your API key

    private static String fetchWeatherData(String city) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Add this line to check the response code

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println("Response Data: " + response.toString()); // Add this line to check the response data

                // Parse JSON response
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response.toString());

                // Extract forecast list
                JSONArray forecastList = (JSONArray) jsonObject.get("list");

                // Build the forecast information string for the next 5 days
                StringBuilder forecastInfo = new StringBuilder("Weather Forecast for the next 5 days:\n");
                for (int i = 0; i < 5; i++) {
                    JSONObject forecastData = (JSONObject) forecastList.get(i * 8); // Data for every 24 hours
                    long dateTime = (long) forecastData.get("dt");
                    JSONObject mainObj = (JSONObject) forecastData.get("main");
                    double temperatureKelvin = (double) mainObj.get("temp");
                    long humidity = (long) mainObj.get("humidity");
                    JSONArray weatherArray = (JSONArray) forecastData.get("weather");
                    JSONObject weather = (JSONObject) weatherArray.get(0);
                    String description = (String) weather.get("description");

                    double temperatureCelsius = temperatureKelvin - 273.15;

                    // Append forecast information for each day
                    forecastInfo.append("Date: ").append(new Date(dateTime * 1000)).append("\n");
                    forecastInfo.append("Description: ").append(description).append("\n");
                    forecastInfo.append("Temperature: ").append(temperatureCelsius).append(" Celsius\n");
                    forecastInfo.append("Humidity: ").append(humidity).append("%\n\n");
                }

                return forecastInfo.toString();
            } else {
                return "Failed to fetch weather data. HTTP Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to fetch weather data.";
        }
    }


    private static void createHomePanel() {
        homePanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to Weather Forecast App", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        homePanel.add(welcomeLabel, BorderLayout.NORTH);
    
        
        JTextPane descriptionPane = new JTextPane();
        descriptionPane.setContentType("text/html"); // Set content type to HTML
        descriptionPane.setEditable(false);
        descriptionPane.setPreferredSize(new Dimension(300, 200)); // Set preferred size
        descriptionPane.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font
    
        String htmlContent = "<html>"
            + "<body>"
            + "<h1 style='font-size: 24px;'>Weather Forecasting</h1>"
            + "<p style='margin-left: 20px; font-size: 16px;'><b>Introduction:</b></p>"
            + "<ul>"
            + "<li style='font-size: 16px;'>Weather forecasting is the prediction of the state of the atmosphere for a given location using the application of science and technology.</li>"
            + "<li style='font-size: 16px;'>This includes temperature, rain, cloudiness, wind speed, and humidity.</li>"
            + "</ul>"
            + "<p style='margin-left: 20px; font-size: 16px;'><b>Practical applications of weather forecasting:</b></p>"
            + "<ul>"
            + "<li style='font-size: 16px;'>Systematic weather records were kept after the invention of the instruments for measuring atmospheric conditions during the 17th century.</li>"
            + "<li style='font-size: 16px;'>Undoubtedly, these early records were employed mainly by those engaged in agriculture.</li>"
            + "<li style='font-size: 16px;'>Planting and harvesting can be planned better and carried out more efficiently if all the long-term weather patterns are estimated in advance.</li>"
            + "</ul>"
            + "</body>"
            + "</html>";
    
        descriptionPane.setText(htmlContent);
        
        homePanel.add(new JScrollPane(descriptionPane), BorderLayout.CENTER);
    
        JButton weatherButton = new JButton("Weather Conditions");
        weatherButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size for the button
        weatherButton.setPreferredSize(new Dimension(200, 50)); // Set preferred size
        weatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(weatherPanel);
                frame.revalidate();
            }
        });
        homePanel.add(weatherButton, BorderLayout.SOUTH);
    
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size for the button
        homeButton.setPreferredSize(new Dimension(200, 50)); // Set preferred size
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(homePanel);
                frame.revalidate();
            }
        });
        homePanel.add(homeButton, BorderLayout.NORTH);
    }
    
    

    private static void createWeatherPanel() {
        weatherPanel = new JPanel(new BorderLayout());
    
        JPanel inputPanel = new JPanel();
        locationField = new JTextField(15);
        fetchButton = new JButton("Fetch Weather");
        inputPanel.add(locationField);
        inputPanel.add(fetchButton);
        weatherPanel.add(inputPanel, BorderLayout.NORTH);
    
        weatherDisplay = new JTextArea(10, 30);
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size to 16
        weatherPanel.add(new JScrollPane(weatherDisplay), BorderLayout.CENTER);
    
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = locationField.getText();
                String weatherInfo = fetchWeatherData(city);
                weatherDisplay.setText(weatherInfo);
            }
        });
    
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(homePanel);
                frame.revalidate();
            }
        });
        weatherPanel.add(homeButton, BorderLayout.SOUTH);
    }
    
    private static void createAndShowGUI() {
        frame = new JFrame("Weather Forecast App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        createHomePanel();
        createWeatherPanel();

        frame.setContentPane(homePanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
