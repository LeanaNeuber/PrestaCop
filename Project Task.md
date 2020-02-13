# Project Task

## Situation

### Client
PrestaCop, a company specializing in service delivery for police forces, wants to create a drone service to help police systems make parking tickets.
A camera with a pattern recognition software identifies license plates and characterizes infractions.
PrestaCop has teams to develop the drone and does work. The first prototype is finished it can qualify some infraction.However the drone service comes with a software as servic that PrestaCop struggle to create.

### Drone Description
Each drone send message regularly, each standard message contains : drone location, time, drone id.
In the event of a violation, the drone sends separately:
* 1 image
* a standard message with additional field : a violation code describing the nature of the offense, image id

### Alert
When the drone can not qualify a possible offense with accuracy, its message’s violation code indicate it requires human intervention. A human operator (probably a NYPD officer) can then take control of the drone Taking control of the drone is done through an already existing software using the drone id.
In doing so the employee is free to observe the vehicle from any angle while driving the drone. Once his observation is over he may send a new message with the right violation code. (Example a police officer will take control of the drone in order to read a car’s plate that the drone can’t read).
After a few tests performed prestacop estimates that this represents 1% of observed violations.

### Statistics
PrestaCop is convinced that we need to keep every drone’s messages in order to make statistics and improve their services. But they still don’t know what kind of question/statistic they will want to address.
PrestaCop has forged a partnership with the New York police to retrieve the history of their tickets data. They want to use NYPD historical data to improve those statistics thus they want to transform it to its equivalent drone data to feed their information system. This historical data is existing data on tickets previously written by NYPD (without Prestacop’s system).
NYPD poses 2 constraints: its computers are old and not very powerful, This history is stored in a large CSV.

### Failed Attempt
To create a POC PrestaCop hired a team of data-scientists and the Prestacop manager expected this team of data-scientists to provide a program that uses few memory resources and can send to PrestaCop computers (or cloud) NYPD historical data.
Despite all their efforts, PrestaCop's data teams have not been able to set up a program that is light enough to send them data from New York police computers.


## Preliminary questions
1. What technical/business constraints should the architecture meet to fulfill the requirement described by the customer in paragraph «​Statistics» ​? (In other words the customer has express some needs, some existing solutions, it comes with limitations).
So what kind of component(s) (listed in the lecture) will the architecture need?
> It's important that a huge amount of data can be processed, from different sources (drones + NYPD data). As it says in the paragraph, all data should be stored, therefore some kind of storage is needed. For the System on the NYPD computers, we need it to use as few compute resources as possible. Also: The data from the NYPD must be cleaned and transformed to the right format.

> **Possible technologies**: Streams for the drone messages (they are of a fixed format and small, near real time, it fits perfectly), data loss must be avoided, no data lake as we have small messages, the best would be a no-sql databank that is partition tolerant and consistent. Both the drone and the NYPD system publish into the stream, and the PrestaCop system reads the messages and works on them in a distributed manner (Spark Streams, Spark, HDFS)

2. Same question with the paragraph «Alert»
> For this question, it is very important that the system is near real-time, because if an alert occurs, it must be handled very quickly. Streams are a perfect solution for it, as they are near real-time. One could maybe think of a stream only for those alerts, and the system handles those with priority..? (is that possible?)

3. What mistake(s) from Prestacop can explain the failed attempt?
> The mistake was to hire data scientist (responsible for machine learning and algorithms) rather than data engineers. They would have known how to clean the data and write reliable software that can run without using too many resources. Another mistake is that the goals are not clear enough, e.g. what kind of statistics are required.

4. Prestacop has likely forgot some technical information in the regular message sent
by the drone. In the future this information could help Prestacop make its product much more profitable. Which information?
> Current standard message: location, time, id
> Current violation message: location, time, id, photo, image id, violation code
> The license plate number?
> The precinct responsible?
> Probably if they bought a parking ticket, then for how long..? They could come back?

## Project
PrestaCop understand this is beyond their team limits, it can not put in place an information system to deal with the drone’s data. PrestaCop asks you for advice to design an architecture allowing them to create a product they could sell to different police forces. Ideally you should also tell PrestaCop how to power this product with NYPD data.
It's up to you to report and recommend what to do.

