//**Blynk Setup 
//**Manual feed tab
//v1 = manual feed button
//v3 = time lcd
//v4 = date lcd
//v5 = dispense amount weight (custom)
//**Scheduled feed tab
//v6 = first scheduled dispense (am)
//v7 = second scheduled dispense (pm)
//v8 = toggle for scheduling (manual mode)


#define BLYNK_PRINT Serial
#include <ESP8266_Lib.h>
#include <BlynkSimpleShieldEsp8266.h>
#include <WidgetRTC.h> //real time clock for scheduling
#include <Wire.h>
#include <Servo.h>
#include <HX711.h> 

// Setup for letting esp8266 connect to blynk app 
char auth[] = "jFZEpOoRfM6_OyrbRoU_Sq46khv0_T57";
char ssid[] = "Reiber";
char pass[] = "1110000111";
#define ESP8266_BAUD 115200
ESP8266 wifi(&Serial);

//setup for load cell
#define LOADCELL_DOUT_PIN  3
#define LOADCELL_SCK_PIN  2
HX711 scale;
float calibration_factor = 470000;

Servo servo;
int angle = 0;
int scheduler=0;
int timeornot=0; //sets timer off by default


//lcd setup for blynk
WidgetLCD lcd(V3);
WidgetLCD lcd1(V4);
//for rtc library and blynk
BlynkTimer timer; 
WidgetRTC rtc;

void schedulechecker();

BLYNK_CONNECTED(){
  rtc.begin(); //sets up time sync with blynk app
}

void setup() {
  servo.attach(12); //motor control to pin 12, power to 5v, gnd to gnd
  
  //serial monitor readout for testing
  Serial.begin(9600);
  delay(10); 
  Serial.begin(ESP8266_BAUD);
  delay(10);
  
  //blynk connection with details given above
  Blynk.begin(auth, wifi, ssid, pass);
  //enable realtime clock through esp8266
  rtc.begin();
  setSyncInterval(10*60); //sets the sync check interval to 10 minutes
  
  //scale setup
  scale.begin(LOADCELL_DOUT_PIN, LOADCELL_SCK_PIN);
  scale.set_scale(calibration_factor);
  scale.tare(); //Reset the scale to 0
  long weightreading = scale.get_units(10);
}

void loop() {
  Blynk.run();
  timer.run();
  
}

BLYNK_WRITE(V1) //Button Widget is writing to pin V1, manual feed
{
  int feed = param.asInt();
  int targetweight = param.asInt(); 
  long weightreading = scale.get_units(10);
  String currentTime = String(hour()) + ":" + minute() + ":" + second();
  String currentDate = String(month()) + "/" + day() + "/" + year();
  
  if (feed==HIGH){
 void loop(); {  
    if (weightreading << targetweight){
      servo.write(angle + 180);
      weightreading = scale.get_units(10);
      delay(500); 
        }
     }
  }
  Serial.println("Feeding");
  Blynk.virtualWrite(V1, LOW);
  lcd.clear();
  lcd1.clear();
  lcd.print(0,0, "Time Last Fed: ");
  lcd.print(0,1, currentTime);
  lcd1.print(0,0, "Date Last Fed: ");
  lcd1.print(0,1, currentDate);
//weight reached
  Serial.println("Food has been Dispensed");
} 

BLYNK_WRITE(V6) //morning pin
{ if  (param.asInt() == 1)
  {timeornot=1;
  }
  if (param.asInt()==0)
  {timeornot=0;
  }
}

BLYNK_WRITE(V7) //evening pin
{ if  (param.asInt() == 1)
  {timeornot=1;
  }
    if (param.asInt()==0)
  {timeornot=0;
  }
}

BLYNK_WRITE(V8) // scheduler on or off
{
  if (param.asInt() == 1) {
    scheduler=1;
    timeornot=0;
    Serial.println("Scheduled feeding is ON");
  }
  if (param.asInt() == 0){
    scheduler=0;
    Serial.println("Scheduled feeding Off");
    }
}

void schedulerchecker() //what to do at scheduled time
{
  int targetweight = 1; //ph
  String currentTime = String(hour()) + ":" + minute() + ":" + second();
  String currentDate = String(month()) + "/" + day() + "/" + year();
  long weightreading = scale.get_units(10);
  if (scheduler==1 && timeornot==1) //autofeed times
  { 
  Blynk.notify("Auto Fed @"+ currentTime);
  void loop(); {  //checking weight on loadcell, if below target, dispense more
    if (weightreading << targetweight){
      servo.write(angle + 180);
      weightreading = scale.get_units(10);
      delay(500); 
        }
     }
  Serial.println("Scheduled Feeding");
  Blynk.virtualWrite(V1, LOW);
  lcd.clear(); //clears the last feed 
  lcd1.clear();
  lcd.print(0,0, "Time Auto Fed: "); //stores most recent scheduled feed
  lcd.print(0,1, currentTime);
  lcd1.print(0,0, " Date Auto Fed: ");
  lcd1.print(0,1, currentDate);
//turn of motor
  Serial.println("Fed");
  
}
timeornot=0;
}
