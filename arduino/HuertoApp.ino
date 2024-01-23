#include <TaskScheduler.h>
#include <ESP32Firebase.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#define DHTPIN 13
#define DHTTYPE DHT11
#define LED_PIN 25

#define _SSID "NETLIFE-BORBOR"            // Tu SSID de WiFi
#define _PASSWORD "borbor2021"       // Tu contraseña de WiFi
#define REFERENCE_URL "https://huertoapp-38033-default-rtdb.firebaseio.com/"

Firebase firebase(REFERENCE_URL);
DHT_Unified dht(DHTPIN, DHTTYPE);
uint32_t delayMS;

int time_lectura;

Scheduler taskScheduler;

void leerEstadoTask();
void escribirTemperaturaTask();

Task leerEstado(1000, TASK_FOREVER, &leerEstadoTask, &taskScheduler);
Task escribirTH(1000, TASK_FOREVER, &escribirTemperaturaTask, &taskScheduler);

void setup() {
  Serial.begin(9600);
  pinMode(LED_PIN, OUTPUT);
  dht.begin();
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(1000);

  Serial.print("Connecting to: ");
  Serial.println(_SSID);
  WiFi.begin(_SSID, _PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("-");
  }

  sensor_t sensor;
  dht.temperature().getSensor(&sensor);
  dht.humidity().getSensor(&sensor);
  delayMS = sensor.min_delay / 1000;
  time_lectura = firebase.getInt("actualizacion");
  Serial.println(time_lectura);

  

  // Programar tareas
  taskScheduler.addTask(leerEstado);
  taskScheduler.addTask(escribirTH);
  escribirTH.enable();
  leerEstado.enable();
}

void loop() {
  taskScheduler.execute();
  delay(1);
}

void leerEstadoTask() {
  String estado = firebase.getString("foco/estado");
  if (estado == "ON") {
    digitalWrite(LED_PIN, HIGH);
    Serial.println("ON");
  } else {
    digitalWrite(LED_PIN, LOW);
    Serial.println("OFF");
  }
  leerEstado.setInterval(1000);
}

void escribirTemperaturaTask() {
  delay(time_lectura);

  sensors_event_t event;
  dht.temperature().getEvent(&event);
  if (!isnan(event.temperature)) {
    Serial.print("Temperature: ");
    Serial.print(event.temperature);
    firebase.setFloat("lectura/temperatura", event.temperature);
    Serial.println("°C");
  } else {
    Serial.println("Error reading temperature!");
  }

  dht.humidity().getEvent(&event);
  if (!isnan(event.relative_humidity)) {
    Serial.print("Humidity: ");
    Serial.print(event.relative_humidity);
    firebase.setFloat("lectura/humedad", event.relative_humidity);
    Serial.println("%");
  } else {
    Serial.println("Error reading humidity!");
  }

  time_lectura = firebase.getInt("actualizacion");

  escribirTH.setInterval(long(time_lectura));
}
