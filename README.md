# API Reference

The API is split into ingoing and outgoing messages...\
Max schreib hier ma kacc

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