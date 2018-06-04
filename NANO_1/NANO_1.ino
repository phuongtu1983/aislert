#define relay1 2  //red light
#define relay2 3  //yellow light
#define relay3 4  //normal sound
#define relay4 5  //emergency sound

int oldRelay;

void setup() {
  Serial.begin(9600);
  delay(100);

  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);
  pinMode(relay3, OUTPUT);
  pinMode(relay4, OUTPUT); 
  digitalWrite(relay1, LOW);
  digitalWrite(relay2, LOW);
  digitalWrite(relay3, LOW);
  digitalWrite(relay4, LOW);

  oldRelay = 0;
}

void loop() {
  if (Serial.available() > 0){
    
    int relay = Serial.read();

    if (relay == 65 && relay != oldRelay){
      //red
      oldRelay = relay;
      digitalWrite(relay1, HIGH);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, LOW);
    }else if (relay == 66 && relay != oldRelay){
      //red + normal sound
      oldRelay = relay;
      digitalWrite(relay1, HIGH);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, HIGH);
      digitalWrite(relay4, LOW);
    }else if (relay == 67 && relay != oldRelay){
      //red + emergency sound
      oldRelay = relay;
      digitalWrite(relay1, HIGH);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, HIGH);
    }else if (relay == 68 && relay != oldRelay){
      //yellow
      oldRelay = relay;
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, HIGH);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, LOW);
    }else if (relay == 69 && relay != oldRelay){
      //yellow + normal sound
      oldRelay = relay;
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, HIGH);
      digitalWrite(relay3, HIGH);
      digitalWrite(relay4, LOW);
    }else if (relay == 70 && relay != oldRelay){
      //yellow + emergency sound
      oldRelay = relay;
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, HIGH);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, HIGH);
    }else if (relay == 71 && relay != oldRelay){
      //turn off
      oldRelay = relay;
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, LOW);
      digitalWrite(relay3, LOW);
      digitalWrite(relay4, LOW);
    }
  }
  
  delay(1000);
}
