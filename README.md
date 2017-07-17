# Akka - Kafka Workshop

---

This is a training exercise designed to give a general knowledge about how an Akka application can interact with Kafka, adding, on top of a dummy application, some interesting features to be used in production.

---

## Description of the base project:
We need an application that reads messages from a Kafka topic called `workshop_akka_training`. Messages are strings representing an accountId.

Kafka message examples: 

```
dummy
foo
bar
```

----

Every time a message is read, it is handled by an actor who stores a Map containing how many messages have been received by accounts, and the date time when the last message was received for that account.

Additionally, we want an endpoint to expose the number of received messages for a given account:

    POST /akka-training/accounts/counter

----

### Request body

```javascript
{
  "accountId": "dummy"
}
```

### Response body

```javascript
{
  "accountId": "dummy",
  "counter": 1,
  "lastModificationDateTime": "2017-02-12T14:01:00.000"
}
```

---

### Steps:


1. Create an akka-http application.
2. Create an actor whos internal state will store a 
  `Map[String, AccountState]` where the String is the `accountId` and the AccountState is a case 
  class defined as follows:
    ```
    AccountState(
      counter: Int, 
      modificationDateTime: LocalDateTime
    )
    ```
3. Create an endpoint that receives a json string with 
  the same shape we described above, and returns the 
  AccountState for the given accountId.
4. Make the previously created actor handle its internal 
  state every time a new kafka message is received.

---

### Notes - Versions to use:
        
        - Circe: 0.5.4
        - Akka: 2.4.10
        - Kafka: 0.10.0.1
        - Scala: 2.11

---

### Acceptance criteria for the base project

Publishing a message to `workshop_akka_training` with an `<account>` string
When Hitting the endpoint requesting the same `<account>` string
Then The response json shows the `AccountState` json respresentation. (Number of received messages and last modification dateTime)

---

## Exercise 1
A third party service changed the shape of the messages we are reading to update our account stats. We now need the kafka string message to be a JSON string with the following shape:

```javascript
{
  "accountId": "dummy"
}
```

##### Acceptance criteria:

Publishing a message to `workshop_akka_training` with this shape and check if the counter has been increased for this particular accountId.



---

## Exercise 2

Our biz department requires us to implement Unit Tests as part of our commitment, so they could be confident enough on the product we are implementing before going to live.
They require us to have +65% code covered with UTs.

##### Acceptance criteria:

Run `sbt coverage test coverageReport` and check if covers more than 65%.

---

## Exercise 3

Our customer service department is asking for a new endpoint `GET /akka-training/accounts/counter`. This endpoint would return a list of accounts and the number of messages received. See a response example:

```javascript
{
  "dummy": 1,
  "foo": 4,
  "bar" 2
}
```
    
##### Acceptance criteria:

A new customized `Encoder[AccountState]` should be done.

---

## Exercise 4

For performance purposes we want to Passivate our actors, so we can save memory. At the same time, we need to put in place a strategy to recover from a passivation, so the actor will remain in the same final state it was before the passivation. 
It has been decided that this recovery strategy will be built over Cassandra, ussing the Akka Persistence plugin.

```"com.typesafe.akka" %% "akka-persistence-cassandra" % "0.18"```

##### Acceptance criteria:

The state of the counters should be the same after shutting down the app, and restarting it.

---

## Exercise 5

Create an [ADT](http://tpolecat.github.io/presentations/algebraic_types.html#1) to represent the messages (`Message`) that are flowing through Kafka, where incoming data are called commands and outcoming ones are events. `Message` have always an `id: String`, a `msgType: String` and a `payload: Payload`, where the payload could be `Command` or `Event`.

Add a new command `IncreaseAccountCommand` to the ADT which represents the incoming message in the Ejercise 1, and change the consumer in order to expect always a `Message[P <: Payload]`.

##### Acceptance criteria:

The app should be working as before when we deliver this message:

```javascript
{
  "id": "my-random-id",
  "msgType": "IncreaseAccountCommand",
  "payload" : {
    "accountId": "dummy"
  }
}
```

---

## Exercise 6

We need to deliver an event to the topic `akka_training_events` when the counter of an account is increased. The payload could be named as `AccountIncreasedEvent` and contains the `accountId` and the current `counter`.

##### Acceptance criteria:

We are getting at the topic `akka_training_events` a message as below:

```javascript
{
  "id": "my-random-id",
  "msgType": "AccountIncreasedEvent",
  "payload" : {
    "accountId": "dummy",
    "counter": 4
  }
}
```

---

## Exercise 7

Create a case class `AkkaTrainingConfig` which has a list of Topic (so far), where Topic is just a string.
Set the topics as config vars in the `application.conf` file, and instantiate a proper `AkkaTrainingConfig`.

##### Acceptance criteria:

The app should be working as before, but we have no longer hardcoded `topic` at all.

---

## Exercise 8 

Implement a [ClusterSharding](http://doc.akka.io/docs/akka/current/scala/cluster-sharding.html) to distribute our actor created in the base project, where:

- `ExtractEntityId` is `(message.payload.accountId, message)` for commands

- `ExtractShardId` is `(message.payload.accountId % shardSize).toString` for commands.

