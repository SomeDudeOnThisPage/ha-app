# API Reference

The API is split into ingoing and outgoing messages from the **Applications' Point of View**!
Ingoing messages are sent only from the WSN to the App, Outgoing only from the App to the WSN.
This means, when developing a WSN for this App, you have to be able to **receive** all **outgoing** messages, and be able to **send** all **ingoing** messages.\
Many messages are symmetric, meaning they are both **in-** and **outgoing**!\
\
Certain rules apply to messages:
1. A message has a length of **five bytes**.
2. A message starts with an **opcode** with a length of **1B**. Available opcodes are described in detail below.
3. Bytes 2-4 are **data bytes** used for communication. The type of data varies depending on the opcode.
4. The fifth byte is a **carriage-return delimiter (0x0D)** used to split messages.

---

### Begin Initialization

**Type:** In- & Outgoing

**Opcode:** 0x00

**Description:** starts the initialization of the Application. This is sent by the application once the user has loaded a model and
selected a serial port. **The WSN must respond with a symmetrical start_init packet after at least 5 seconds, or the application
considers the connection invalid and disconnects!**\
Once start_init has been returned, the application enters **Initialization State** during which the WSN should send
**all** available state vectors **once**. The WSN should then end the initialization with an **End Initialization Message**. 

<br>

| Data Field  | Data      | Type   | Description                            | Possible Values |
|:------------|:----------|:-------|:---------------------------------------|:----------------|
| **byte #1** | opcode    | (-)    | Begin Initialization Opcode            | **0x00**        |
| **byte #2** | null      | (-)    | unused                                 | (-)             |
| **byte #3** | null      | (-)    | unused                                 | (-)             |
| **byte #4** | null      | (-)    | unused                                 | (-)             |
| **byte #5** | delimiter | (-)    | Message Delimiter                      | **0x0D**        |

##### Usage Example
The Application requests the begin of the initialization phase.
````
Hex: 0x000000000D
````
---

### End Initialization

**Type:** Ingoing

**Opcode:** 0x01

**Description:** ends the initialization of the Application

<br>

| Data Field  | Data      | Type   | Description                            | Possible Values |
|:------------|:----------|:-------|:---------------------------------------|:----------------|
| **byte #1** | opcode    | (-)    | Begin Initialization Opcode            | **0x01**        |
| **byte #2** | null      | (-)    | unused                                 | (-)             |
| **byte #3** | null      | (-)    | unused                                 | (-)             |
| **byte #4** | null      | (-)    | unused                                 | (-)             |
| **byte #5** | delimiter | (-)    | Message Delimiter                      | **0x0D**        |

### Usage Example
The WSN signals the Application that all data has been sent.
````
Hex: 0x010000000D
````
---
### Light State Switching

**Type:** In- & Outgoing

**Opcode:** 0x02

**Description:** Switches a light to either its' **on** or **off** state.

<br>

| Data Field  | Data      | Type             | Description                            | Possible Values                   |
|:------------|:----------|:-----------------|:---------------------------------------|:----------------------------------|
| **byte #1** | opcode    | (-)              | Light Switch Opcode                    | **0x02**                          |
| **byte #2** | room ID   | **unsigned char** | Numerical ID of the Room.              | [**0x00** (0) - **0xFF** (255)]   |
| **byte #3** | light ID  | **unsigned char** | Numerical ID of the Light.             | [**0x00** (0) - **0xFF** (255)]   |
| **byte #4** | state     | **boolean**      | Desired state of the light.            | [**0x00** (off) \| **0x01** (on)] |
| **byte #5** | delimiter | (-)              | Message Delimiter                      | **0x0D**                          |

##### Usage Example
Switch on light #1 in room #3.
````
Hex: 0x020301010D
````

---
### Light Mode Switching
**Type:** In- & Outgoing

**Opcode:** 0x03

**Description:** Switches a lights' mode to either **manual** or **auto**.

<br>

| Data Field  | Data      | Type             | Description                            | Possible Values                   |
|:------------|:----------|:-----------------|:---------------------------------------|:----------------------------------|
| **byte #1** | opcode    | (-)              | Light Switch Opcode                    | **0x03**                          |
| **byte #2** | room ID   | **unsigned char** | Numerical ID of the Room.              | [**0x00** (0) - **0xFF** (255)]   |
| **byte #3** | light ID  | **unsigned char** | Numerical ID of the Light.             | [**0x00** (0) - **0xFF** (255)]   |
| **byte #4** | mode      | **boolean**      | Desired mode of the light.             | [**0x00** (manual) \ **0x01** (auto)] |
| **byte #5** | delimiter | (-)              | Message Delimiter                      | **0x0D**                          |

##### Usage Example
Switch light #3 in room #2 to **auto**

````
Hex: 0x030203010D 
````
---
### Temperature

**Type:** Ingoing

**Opcode:** 0x04

**Description:** The WSN tells the application the current temperature value of a specific room.
                 
<br>

| Data Field  | Data      | Type             | Description                            | Possible Values                   |
|:------------|:----------|:-----------------|:---------------------------------------|:----------------------------------|
| **byte #1** | opcode    | (-)              | Light Switch Opcode                    | **0x04**                          |
| **byte #2** | room ID   | **unsigned char** | Numerical ID of the Room.              | [**0x00** (0) - **0xFF** (255)]   |
| **byte #3** | temp. value| **signed char**  | integer places  (xx.00)                | [**0xFF** (-128) - **0x7F** (127)]|
| **byte #4** | temp. value| **unsigned char**| decimal places  (00.xx)                | [**0x00** (0) - **0x64** - (100)] |
| **byte #5** | delimiter | (-)              | Message Delimiter                      | **0x0D**                          |

##### Usage Example
The WSN telling the App that the temperature in room #0 is 23,5°C:

````
Hex: 0x040017320D
````
---

### Temperature Reference

**Type:** In- & Outgoing

**Opcode:** 0x05

**Description:** the Application sets a temperature reference for heating control. 
This message is only ingoing during the **Initialization Phase**.

<br>

| Data Field  | Data      | Type             | Description                            | Possible Values                   |
|:------------|:----------|:-----------------|:---------------------------------------|:----------------------------------|
| **byte #1** | opcode    | (-)              | Light Switch Opcode                    | **0x05**                          |
| **byte #2** | room ID   | **unsigned char** | Numerical ID of the Room.              | [**0x00** (0) - **0xFF** (255)]   |
| **byte #3** | temp. value| **signed char**  | integer places  (xx.00)                | [**0xFF** (-128) - **0x7F** (127)]|
| **byte #4** | temp. value| **unsigned char**| decimal places  (00.xx)                | [**0x00** (0) - **0x64** - (100)] |
| **byte #5** | delimiter | (-)              | Message Delimiter                      | **0x0D**                          |


##### Usage Example
The Application tells the WSN to cool / heat the room #1 to 21.00°C:

````
Hex: 0x050115000D
````