# API Reference

The API is split into ingoing and outgoing messages from the **Applications' Point of View**!\
Ingoing messages are sent only from the WSN to the App, Outgoing only from the App to the WSN.\
This means, when developing a WSN for this App, you have to be able to **receive** all **outgoing** messages, and be able to **send** all **ingoing** messages.\
Many messages are symmetric, meaning they are both **in-** and **outgoing**!\
For an ingoing message, the method **update (String data)** is called. Then the received String is split two times the following way:

1. Split by _**\r\n**_ to achieve the availability to process
multiple messages.
2. Split by " " (_space_) to extract information from the String.


Afterwards, the extracted data goes into further process.
The procedure consists of **three** basic steps, which apply to every given String.

1. Check, _which operation_ is aimed to be called through a unique name.
2. _Verify_, whether the received message and its parts are compatible to the method.
3. _Call_ of the actual method.

The in- and outgoing methods are described in detail below.

---

### light_switch

**Type:** 
In- & Outgoing

**Description:**
Switches a light to either its' <font color='#0099ff'>**on**</font> or <font color='#0099ff'>**off**</font> state. 

<br/>

| Data Field | Description | Possible Values |
| ---------- | ----------- | --------------- |
| <font color='#0099ff'>**int**</font> roomID | The numerical ID of the room | any <font color='#0099ff'>**int**</font> |
| <font color='#0099ff'>**int**</font> lightID | The numerical ID of the light | any <font color='#0099ff'>**int**</font> |
| <font color='#0099ff'>**String**</font> state | The state of the light | **\[<font color='#0099ff'>on</font>\|<font color='#0099ff'>off</font>\]** |

##### Usage Example
Telling the App that light **#0** in room **#0** has been switched on:
````
light_switch 0 0 on
````

---

### light_mode

**Type:** 
In- & Outgoing

**Description:**
Switches a lights' mode to either <font color='#0099ff'>**manual**</font> or <font color='#0099ff'>**automatic**</font>.

<br/>

| Data Field | Description | Possible Values |
| ---------- | ----------- | --------------- |
| <font color='#0099ff'>**int**</font> roomID | The numerical ID of the room | any <font color='#0099ff'>**int**</font> |
| <font color='#0099ff'>**int**</font> lightID | The numerical ID of the light | any <font color='#0099ff'>**int**</font> |
| <font color='#0099ff'>**String**</font> mode | The mode of the light | **\[<font color='#0099ff'>auto</font>\|<font color='#0099ff'>manual</font>\]** |

##### Usage Example
The App telling the WSN that light **#0** in room **#0** has been switched back to automatic:
````
light_mode 0 0 auto
````

---

### temperature
**Type:**
Ingoing

**Description:**
The WSN tells the application the **current temperature** value
of a specific room. <br/>

| Data Field | Description | Possible Values|
|------------|-------------|----------------|
|<font color='#0099ff'>**int**</font> roomID | The numerical ID of the room | any <font color='#0099ff'>**int**</font>|
|<font color='#0099ff'>**int**</font> value| the current temperature of the room in **°C**|  any <font color='#0099ff'>**float**| 

##### Usage Example
The WSN telling the App that the temperature in room **#0** is 23,5°C:
````
temperature 0 23.5
````

---
### temperature_reference
**Type:** In- & Outgoing <br>

**Description:** the Application sets a temperature reference for heating control. This message is only ingoing during the
[**Initialization Phase**](#start_init).


| Data Field | Description | Possible Values|
|------------|-------------|----------------|
|<font color='#0099ff'>**int**</font> roomID | The numerical **ID** of the room | any <font color='#0099ff'>**int**</font>|
|<font color='#0099ff'>**int**</font> value| the reference temperature of the room in **°C**|  any <font color='#0099ff'>**float**| 

##### Usage Example
The Application tells the WSN to cool / heat the room to 21.00°C:
````
temperature_reference 0 21.00
````
---
### start_init
**Type:** In- & Outgoing
**Description:** starts the initialization of the WSN. This is sent by the application once the user has loaded a model and
selected a serial port. **The WSN must respond with a symmetrical start_init packet after at least 5 seconds, or the application
considers the connection invalid and disconnects!**\
Once start_init has been returned, the application enters **Initialization State** during which the WSN should send
**all** available state vectors **once**. The WSN should then end the initialization with **end_init**. 
##### Usage Example
The Application requests the begin of the initialization phase.
````
start_init
````
---
### end_init
**Type:** Ingoing
**Description:** ends the initialization of the WSN.
##### Usage Example
The WSN signals the Application that all data has been sent.
````
end_init
````
---