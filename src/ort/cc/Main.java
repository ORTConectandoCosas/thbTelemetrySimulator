package ort.cc;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.*;

import java.awt.event.ItemEvent;
import java.util.concurrent.TimeUnit;

public class Main {
    /*
       ThingsBoard parameters for MQTT
     */
    private String thbServer = "tcp://demo.thingsboard.io:1883";
    private String publishTopic = "v1/devices/me/telemetry";

    private String clientId = "ASIMLATOR";
    private String deviceToken = "30Y3Hf2lC3N43UqWusNC";

    //mqtt connection parameters
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttConnectOptions connectOptions = new MqttConnectOptions();
    private MqttClient thbMqttClient = null;

    // data to simulate sensor values
    private int temperatureData = 10;
    private int humidityData = 40;

    public static void main(String[] args) {
        Main telemetrySimulator = new Main();
        telemetrySimulator.SimulateTelemetry();
        System.exit(0);
    }

    private void SimulateTelemetry()
    {
        this.ConnectToServer();

        while (true) {
            //Build a json with telemetry data
            String jsonString = "{\"temperature\" :" + Integer.toString(temperatureData) + "," + "\"humidity\":" + Integer.toString(humidityData) + "}";
            System.out.println("=>Telemetria enviada: " + jsonString);

            //publish to telemetry topic
            MqttMessage msg = new MqttMessage(jsonString.getBytes());
            msg.setQos(0);

            try {
                //publish telemetry
                thbMqttClient.publish(publishTopic, msg);

                // simulate a delay()
                TimeUnit.MILLISECONDS.sleep(2000);

                ChangeSensorValues();

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } catch (MqttException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    // Connect to MQTT server
    private void ConnectToServer() {
        //Connect to Thingsboard server
        connectOptions.setUserName(deviceToken);
        connectOptions.setMaxInflight(200);

        try {
            // create Mattclient
            thbMqttClient = new MqttClient(thbServer, clientId, persistence);
            connectOptions.setMaxInflight(200);

            //connectOptions.setCleanSession(true);
            thbMqttClient.connect(connectOptions);

            System.out.println("Connecting to server");

        } catch (MqttException e) {
            System.out.println("Mqtt exception" + e.getMessage());
        }

    }

    private void ChangeSensorValues() {
        if (++temperatureData > 45)
            temperatureData = 20;
        if (++humidityData >= 100)
            humidityData = 30;

    }
}
