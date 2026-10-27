
📘 ✔Basic Kafka Chapters (Beginner → Strong Foundation)

These chapters focus on Producer, Consumer, Topic, Partition, Broker — the core building blocks.

1️⃣ Introduction to Kafka

What is Kafka?

Distributed event streaming platform

High throughput

Fault tolerant

Horizontally scalable

========================================

✔Why Kafka?

Event-driven microservices

Log aggregation

Real-time analytics

Messaging system replacement

========================================

2️⃣ ✔Kafka Core Components

This is the most important foundation chapter.

🔹 Broker

Kafka server instance

Stores data

Handles read/write requests

Multiple brokers form a cluster

Example:

Broker 1
Broker 2
Broker 3

Each broker:

Stores partitions

Has unique broker ID
=======================================================
🔹 Topic

Logical category of messages

Like a table in DB

Producers write to topics

Consumers read from topics

Example:

order-events
payment-events
reward-events

==========================================

🔹 Partition

Topic is divided into partitions

Each partition is an ordered, immutable log

Ordering guaranteed per partition

Example:

Topic: order-events
Partition 0
Partition 1
Partition 2

===============================================

Why partitions?

Parallelism

Scalability

Load balancing

🔹 Producer

Sends messages to topic

Can choose partition:

By key

Round robin

Custom partitioner

Message structure:

Key | Value | Offset | Timestamp

🔹 Consumer

Reads messages from topic

Pull-based model

Belongs to consumer group

Consumer group:

Multiple consumers share partitions

One partition → One consumer in group

==========================================================

3️⃣ How Data Flows in Kafka
Producer → Topic → Partition → Broker → Consumer

Step-by-step:

Producer sends message

Broker stores in partition

Offset assigned

Consumer polls messages

Consumer commits offset

==========================================================

4️⃣ Offsets

Unique number per partition

Identifies message position

Managed by consumer group

Two types:

Auto commit

Manual commit

Example:

Partition 0:
Offset 0
Offset 1
Offset 2

=====================================================
5️⃣ Replication & Fault Tolerance

Each partition has:

Leader

Followers (replicas)

Replication factor example:

RF = 3

If leader fails:

Follower becomes leader

=======================================================

6️⃣ Consumer Groups

Important concept.

Example:

Topic → 3 partitions

Consumer Group A:
  Consumer 1 → P0
  Consumer 2 → P1
  Consumer 3 → P2

Rules:

One partition assigned to only one consumer in group

Different groups get full copy of data

============================================================

7️⃣ Kafka Installation & Setup

Install Kafka

Download from:

kafka.apache.org

Start services:

# Start ZooKeeper (older versions)
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
bin/kafka-server-start.sh config/server.properties

(Newer versions use KRaft mode without ZooKeeper.)

===============================================================

8️⃣ Basic Kafka Commands

Create topic:

bin/kafka-topics.sh --create \
--topic order-events \
--bootstrap-server localhost:9092 \
--partitions 3 \
--replication-factor 1

List topics:

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

Produce messages:

bin/kafka-console-producer.sh \
--topic order-events \
--bootstrap-server localhost:9092

Consume messages:

bin/kafka-console-consumer.sh \
--topic order-events \
--from-beginning \
--bootstrap-server localhost:9092

============================================================

9️⃣ Producer Basics (Configuration)

Important configs:

bootstrap.servers=localhost:9092
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer
acks=all
retries=3
🔟 Consumer Basics (Configuration)
bootstrap.servers=localhost:9092
group.id=order-group
key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
auto.offset.reset=earliest
enable.auto.commit=false

==============================================================

1️⃣1️⃣ Message Ordering

Ordering guaranteed within partition

Not across partitions

Use key for consistent ordering

Example:

key = orderId

All messages with same key → same partition.

===============================================================

1️⃣2️⃣ Kafka Use Case Examples

Order processing

Payment processing

Notification service

Reward engine

Log streaming

📊 Visual Architecture Diagram (Basic)
             ┌───────────┐
             │ Producer  │
             └─────┬─────┘
                   │
                   ▼
          ┌──────────────────┐
          │   Kafka Cluster  │
          │ ┌──────────────┐ │
          │ │ Topic        │ │
          │ │ ┌──────────┐ │ │
          │ │ │Partition0│ │ │
          │ │ │Partition1│ │ │
          │ │ │Partition2│ │ │
          │ └──────────────┘ │
          └─────────┬────────┘
                    │
                    ▼
              ┌───────────┐
              │ Consumer  │
              └───────────┘
=======================================================			  
			  
🎯 Beginner → Intermediate Learning Path

✔Install Kafka locally

✔Create topics

✔Produce & consume manually

✔Understand partitions

✔Implement Java Producer

✔Implement Java Consumer

✔Learn consumer groups

✔Learn replication

================================================

We can explain below:

🔥 Kafka with Spring Boot (Producer + Consumer Full Example)

🔥 Kafka Interview Questions (Basic to Advanced)

🔥 Kafka Architecture Diagram Explained

🔥 Kafka vs RabbitMQ Comparison

🔥 Kafka Hands-on Mini Project (Order Processing System)

✔What do you want next? 🚀

Can you explain Chapter 1 with suitable code example step wise step? ️⃣ Introduction to Kafka 
What is Kafka?

Distributed event streaming platform

High throughput

Fault tolerant

Horizontally scalable

Why Kafka?

Event-driven microservices

Log aggregation

Real-time analytics

Messaging system replacement

👍 Let’s go step-by-step and deeply understand 
Chapter 1 — Introduction to Kafka with a simple working example.

Platform reference: Apache Kafka

======================================================================

📘 Chapter 1 – Introduction to Kafka

1️⃣ What is Kafka?

Kafka is a distributed event streaming platform used to:

Publish events (Producer)

Store events (Broker)

Process/consume events (Consumer)

Stream data in real-time

Think of Kafka as a real-time event pipeline between services.

===================================================================

2️⃣ What does "Distributed Event Streaming Platform" mean?

Let’s break it:

🔹 Distributed

Runs on multiple servers (brokers).

🔹 Event

An event is just data like:

{
  "orderId": 101,
  "amount": 5000,
  "status": "CREATED"
}
🔹 Streaming

Data flows continuously (not request-response).

=====================================================

3️⃣ Why Kafka?

Traditional System	Kafka Approach
Direct service calls	Event-driven
Tight coupling	Loose coupling
Hard to scale	Horizontally scalable
Blocking calls	Asynchronous

======================================================

4️⃣ Simple Architecture

Producer → Kafka → Consumer

Example:

Order Service → Kafka → Payment Service

Tracker → Kafka → Reward Engine

App → Kafka → Analytics

5️⃣ Step-by-Step Hands-On Example

We will build:

✔ Producer sends Order event
✔ Kafka stores event
✔ Consumer reads event

🔹 STEP 1 — Start Kafka Locally

Download Kafka from apache website.

Start Kafka (Linux/Mac):

# Start Kafka (KRaft mode in latest versions)
bin/kafka-server-start.sh config/server.properties

Kafka runs on:

localhost:9092
🔹 STEP 2 — Create Topic
bin/kafka-topics.sh --create \
--topic order-events \
--bootstrap-server localhost:9092 \
--partitions 3 \
--replication-factor 1

Now we have:

Topic: order-events
Partitions: 3
🔹 STEP 3 — Create Java Producer
Add Maven Dependency
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>3.6.0</version>
</dependency>
Producer Code
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class OrderProducer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "all");

        Producer<String, String> producer =
                new KafkaProducer<>(props);

        String topic = "order-events";

        String message = "{ \"orderId\": 101, \"amount\": 5000 }";

        ProducerRecord<String, String> record =
                new ProducerRecord<>(topic, "101", message);

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Sent to partition: " 
                        + metadata.partition()
                        + " offset: " 
                        + metadata.offset());
            } else {
                exception.printStackTrace();
            }
        });

        producer.close();
    }
}

What happens here?

Producer connects to Kafka broker

Sends message to order-events

Kafka assigns partition

Kafka assigns offset

Message stored on disk

====================================================================

🔹 STEP 4 — Create Java Consumer
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OrderConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singleton("order-events"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: " 
                        + record.value()
                        + " Partition: " 
                        + record.partition()
                        + " Offset: " 
                        + record.offset());
            }
        }
    }
}

🔥 What Happens Internally?

Producer sends message

Broker stores in partition

Offset assigned (e.g., 0)

Consumer polls message

Consumer processes event
====================================================

6️⃣ How Kafka Achieves High Throughput?

Sequential disk writes

Batching

Zero-copy transfer

Compression support

Kafka can handle:

Millions of messages per second

7️⃣ How Kafka is Fault Tolerant?

If replication factor = 3:

Partition 0
Leader → Broker 1
Follower → Broker 2
Follower → Broker 3

If Broker 1 crashes:

Broker 2 becomes leader

No data loss (if replicated)

8️⃣ How Kafka is Horizontally Scalable?

Add more brokers:

Broker 1
Broker 2
Broker 3
Broker 4 (new)

Kafka automatically distributes partitions.

More brokers → more partitions → more throughput.

==============================================================

9️⃣ Real-World Example

Without Kafka

Order Service → calls → Payment Service
If Payment down → Order fails ❌

With Kafka

Order Service → publishes event
Payment Service → consumes when ready ✅

Loose coupling achieved.

🔟 Why Kafka is Used in Microservices?

Because it provides:

✔ Asynchronous communication
✔ Decoupling
✔ Replay capability
✔ Event-driven architecture
✔ Scalability

🎯 ✔Final Summary:

Kafka is:

✔Distributed

✔Event-driven

✔High-throughput

✔Fault-tolerant

✔Horizontally scalable

✔It acts as a central event backbone between services.

===================================================================

Now we can explain:

🔥 Chapter 2 – Topics, Partitions, Brokers (Deep Internals)✔

🔥 Kafka Architecture Diagram Explained Visually✔

🔥 Spring Boot Kafka Example✔

🔥 Real Microservices Example (Order → Payment → Reward)✔

🔥 Kafka Internal Flow (How message moves inside broker)✔

✔Which direction should we go next? 🚀

yes explain Chapter 2 with complete code example and diagram? 

✔Chapter 2 – Topics, Partitions, Brokers (Deep Internals)

🔥 Kafka Architecture Diagram Explained Visually

🔥 Spring Boot Kafka Example

🔥 Real Microservices Example (Order → Payment → Reward)

🔥 Kafka Internal Flow (How message moves inside broker)

Excellent 🚀

✔Now we move to Chapter 2 – Topics, Partitions, Brokers (Deep Internals) with:

✅ Visual Architecture Diagram

✅ Complete Spring Boot Kafka Example

✅ Real Microservices Flow (Order → Payment → Reward)

✅ Internal Broker Flow (What happens inside Kafka)

✔Platform reference: Apache Kafka

📘 Chapter 2 – Topics, Partitions, Brokers (Deep Internals)

1️⃣ Core Concepts Deep Dive

🔹 1. Topic (Logical Layer)

A Topic is a logical name where messages are written.

Example:

order-events
payment-events
reward-events

Think of Topic like:

✔A database table

✔Or a message channel

✔BUT internally → Topic is split into partitions.

🔹 2. Partition (Physical Storage Unit)

✔A Topic is divided into Partitions.

Each partition:

✔Is an append-only log

✔Stores messages sequentially

✔Has offsets (0,1,2,3…)

Example:

Topic: order-events

Partition 0 → offset 0,1,2
Partition 1 → offset 0,1
Partition 2 → offset 0

✔ Ordering guaranteed per partition
❌ Not guaranteed across partitions

🔹 3. Broker (Kafka Server)

A Broker:

✔Is a Kafka server instance

✔Stores partitions

✔Handles read/write requests

Multiple brokers form a Kafka cluster.

Example cluster:

Broker 1
Broker 2
Broker 3

🔥 Kafka Architecture Diagram (Visually Explained)✔
                   ┌──────────────────────┐
                   │     Producer         │
                   └──────────┬───────────┘
                              │
                              ▼
                 ┌───────────────────────────┐
                 │        Kafka Cluster      │
                 │                           │
                 │  Broker 1                 │
                 │   ├─ order-events-P0      │
                 │   ├─ payment-events-P0    │
                 │                           │
                 │  Broker 2                 │
                 │   ├─ order-events-P1      │
                 │   ├─ reward-events-P0     │
                 │                           │
                 │  Broker 3                 │
                 │   ├─ order-events-P2      │
                 │   ├─ payment-events-P1    │
                 └─────────────┬─────────────┘
                               │
                               ▼
                        ┌─────────────┐
                        │ Consumers   │
                        └─────────────┘
🔥 Internal Partition Replication✔

✔If replication factor = 3

Partition 0:

Leader → Broker 1
Follower → Broker 2
Follower → Broker 3

✔Only Leader handles:

Producer writes

Consumer reads

Followers:

✔Replicate data

✔Take over if leader crashes

🚀 Real Microservices Example

We build:

Order Service → Payment Service → Reward Service

Flow:

Order Service → order-events
Payment Service → payment-events
Reward Service → reward-events

🏗 Step-by-Step Spring Boot Example✔

🔹 STEP 1 — Add Dependencies✔
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

🔹 STEP 2 — application.yml✔
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
	  
🔹 STEP 3 — Create Order Producer✔
@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String orderId) {

        String message = "Order Created: " + orderId;

        kafkaTemplate.send("order-events", orderId, message);

        System.out.println("Order Sent: " + message);
    }
}

🔹 STEP 4 — Create Order Controller✔
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderProducer producer;

    @PostMapping("/{id}")
    public String createOrder(@PathVariable String id) {
        producer.sendOrder(id);
        return "Order Created";
    }
}

Now when calling:

POST /orders/101

✔Kafka receives message.

🔹 STEP 5 — Payment Service Consumer✔
@Service
public class PaymentConsumer {

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void processPayment(String message) {

        System.out.println("Processing Payment for: " + message);

        // After payment success → publish payment event
    }
}

🔹 STEP 6 — Reward Service Consumer✔
@Service
public class RewardConsumer {

    @KafkaListener(topics = "payment-events", groupId = "reward-group")
    public void processReward(String message) {

        System.out.println("Reward Triggered for: " + message);
    }
}

=========================================================
🔥 How Partitioning Works in This Example

If:

order-events → 3 partitions

And you send:

Key = orderId

Kafka:

Hashes key

Assigns same orderId to same partition

Maintains order for that order

🔥 Kafka Internal Flow (Inside Broker)

Now let’s go deep 👇

When producer sends message:

✔Step 1: Metadata Request

Producer asks:

Which broker is leader for partition?

Broker responds with metadata.

✔Step 2: Append to Log

Message appended to partition log file:

log-segment-0001.log

Data stored sequentially:

Offset 0
Offset 1
Offset 2

✔Step 3: Page Cache

Kafka writes to:

OS Page Cache

Not directly disk every time

Flush happens asynchronously

✔Step 4: Replication

Leader sends data to followers.

Followers:

Append to their logs

Send ACK to leader

✔Step 5: High Watermark Update

Leader updates High Watermark when:

min.insync.replicas acknowledged

Only then message visible to consumers.

==========================================================

🔥 How Consumer Reads

Consumer:

Sends fetch request

Broker returns records from offset

Consumer processes

Commits offset

✔Offset stored in:

__consumer_offsets topic

📊 Deep Architecture Summary:

Producer
   ↓
Metadata request
   ↓
Leader partition
   ↓
Append to log
   ↓
Replicate to followers
   ↓
Update high watermark
   ↓
Consumer fetch
   ↓
Offset commit

🎯 Why This Matters for You

Since we work on:

Event reprocessing

Batch flows

Reward engines

Pub/Sub consumers

Understanding:

Partition design

Replication

Offset management

Consumer groups

=====================================================

Is CRITICAL for designing scalable incentive systems.

🚀 Next Level Options

we can now explain:

🔥 Kafka Consumer Groups Deep Dive (Rebalancing + Lag)

🔥 Exactly-Once Processing with Spring Boot

🔥 Kafka Performance Tuning

🔥 Kafka Failure Scenarios Simulation

🔥 Kafka vs Pub/Sub vs RabbitMQ Architecture Comparison

================================================================

Which one should we go deep into next? 🚀

Explain with complete code example 

3️⃣ How Data Flows in Kafka

Producer sends message

Broker stores in partition

Offset assigned

Consumer polls messages

Consumer commits offset

we’ll deeply understand How Data Flows in Kafka step-by-step with:

✅ Internal Flow Explanation

✅ Complete Java Code (Producer + Consumer)

✅ Offset Commit Example (Auto + Manual)

✅ Diagram

✅ What Happens Internally in Broker

Platform reference: Apache Kafka

===================================================

📘 3️⃣ How Data Flows in Kafka

We will explain this pipeline:

Producer → Broker (Partition) → Offset Assigned → Consumer Poll → Offset Commit

🔥 High-Level Visual Flow

Producer
   │
   ▼
Kafka Broker
   │
   ▼
Partition (append-only log)
   │
   ▼
Offset Assigned
   │
   ▼
Consumer Poll
   │
   ▼
Offset Commit

🏗 Step-by-Step Hands-On Example

We will build:

✔ Java Producer
✔ Java Consumer
✔ Manual Offset Commit

🔹 STEP 1 — Create Topic✔

bin/kafka-topics.sh --create \
--topic order-events \
--bootstrap-server localhost:9092 \
--partitions 2 \
--replication-factor 1

Now topic structure:

order-events
  Partition 0
  Partition 1
  
🔹 STEP 2 — Producer Sends Message✔

Add Dependency (Maven)
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>3.6.0</version>
</dependency>

✅ Producer Code✔
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class OrderProducer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "all");

        Producer<String, String> producer = new KafkaProducer<>(props);

        String topic = "order-events";

        for (int i = 1; i <= 5; i++) {

            String key = "order-" + i;
            String value = "Order Created with ID " + i;

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, key, value);

            producer.send(record, (metadata, exception) -> {

                if (exception == null) {
                    System.out.println(
                            "Sent → Partition: " + metadata.partition()
                                    + " Offset: " + metadata.offset()
                                    + " Key: " + key);
                } else {
                    exception.printStackTrace();
                }
            });
        }

        producer.close();
    }
}

🔥 What Happens Internally?

Step 1: Producer Sends Message✔

Producer:

Connects to broker

Requests metadata

Finds leader for partition

Step 2: Broker Stores in Partition✔

Kafka stores message in log file like:

/kafka-logs/order-events-0/000000000.log

Each partition is:

Append-only

Sequential write

Immutable

Step 3: Offset Assigned✔

If partition 0:

Offset 0
Offset 1
Offset 2
Offset 3
Offset 4

Offset is:

Unique per partition

Sequential

Assigned by broker

Producer does NOT control offset.

🔹 STEP 3 — Consumer Polls Messages✔

✅ Consumer Code (Manual Offset Commit)✔

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OrderConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        // IMPORTANT
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("order-events"));

        while (true) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {

                System.out.println(
                        "Received → Partition: " + record.partition()
                                + " Offset: " + record.offset()
                                + " Key: " + record.key()
                                + " Value: " + record.value());

                // Simulate processing
                processOrder(record.value());
            }

            // MANUAL COMMIT AFTER PROCESSING
            consumer.commitSync();
        }
    }

    private static void processOrder(String order) {
        System.out.println("Processing: " + order);
    }
}

🔥 What Happens During Poll?

When consumer.poll() is called:

Consumer sends FetchRequest to broker

Broker returns records from current offset

Consumer processes messages

Consumer commits offset

🔹 STEP 4 — Offset Commit✔

There are two types:

🔹 Auto Commit✔

enable.auto.commit=true

Kafka automatically commits offset every 5 seconds.

Risk:

If app crashes after commit but before processing → data loss.

🔹 Manual Commit (Recommended)✔

consumer.commitSync();

Best practice:

Commit only after successful processing.

Guarantee:

At-least-once delivery

🔥 Where Offset Is Stored?

Offsets are stored in internal Kafka topic:

__consumer_offsets

Each consumer group has its own offset tracking.

📊 Complete Flow Diagram✔

1️⃣ Producer
   |
   |  Send Message
   ▼
2️⃣ Broker (Leader Partition)
   |
   |  Append to log
   |  Assign Offset
   ▼
3️⃣ Partition Log
   |
   ▼
4️⃣ Consumer Poll()
   |
   |  Fetch records
   ▼
5️⃣ Process Message
   |
   ▼
6️⃣ Commit Offset

🧠 Internal Deep Insight✔

When Producer sends:

Data batched

Sent to leader

Leader writes to page cache

Replicates to followers

If acks=all → waits for ISR

When Consumer polls:

Uses last committed offset

Fetches sequential records

Maintains position pointer

====================================================

🔥 What Happens If Consumer Crashes?

If using manual commit:

Uncommitted messages will be reprocessed

Ensures no data loss

Might cause duplicates

This is called:

👉 At-Least-Once Processing

🎯 Summary

Data Flow in Kafka:

Step	What Happens✔

Producer sends	Message goes to broker leader
Broker stores	Appends to partition log
Offset assigned	Sequential number per partition
Consumer polls	Fetches records from offset
Consumer commits	Saves position for recovery

🚀 Next:

can now explain:

🔥 Exactly-Once Processing (Transactional Producer)

🔥 Consumer Group Rebalancing Deep Dive

🔥 Partition Strategy Design

🔥 Kafka Failure Simulation (Crash Scenarios)

🔥 Kafka Internal Storage (Log Segments, Index Files)

=====================================================

Which one should we go deeper into?

4️⃣ Offsets

Unique number per partition

Identifies message position

Managed by consumer group

Two types:

Auto commit

Manual commit


4️⃣ Offsets in Kafka with:

✅ Concept Explanation

✅ Internal Storage Explanation

✅ Auto Commit Example

✅ Manual Commit Example

✅ Failure Scenarios

✅ Diagram

Platform reference: Apache Kafka

======================================================

📘 4️⃣ Offsets in Kafka

1️⃣ What is an Offset?

An Offset is:

A unique sequential number assigned to each message within a partition.

Important:

Offset is per partition

Offset starts from 0

Offset increases sequentially

Offset identifies message position

Example

Topic: order-events
Partitions: 2

Partition 0:
Offset 0 → Order A
Offset 1 → Order B
Offset 2 → Order C

Partition 1:
Offset 0 → Order X
Offset 1 → Order Y

⚠ Offset 0 in partition 0 ≠ Offset 0 in partition 1

Offsets are independent per partition.

2️⃣ Why Offsets Are Important?

Offsets allow Kafka to:

Track consumer progress

Resume after crash

Replay messages

Ensure at-least-once delivery

3️⃣ Who Manages Offsets?

👉 Offsets are managed per consumer group

Each consumer group maintains its own position.

Example:

Topic: order-events

Group A → Offset 5
Group B → Offset 2

Same topic, different offset tracking.

Offsets are stored in internal Kafka topic:

__consumer_offsets
🔥 Visual Offset Flow
Producer
   ↓
Partition Log
   ↓
Offset Assigned
   ↓
Consumer Poll
   ↓
Commit Offset
   ↓
__consumer_offsets (internal topic)

4️⃣ Two Types of Offset Commit
Type	Who commits?	Risk	Use Case
Auto Commit	Kafka automatically	Data loss possible	Simple apps
Manual Commit	Application	Safe	Production systems

🔹 5️⃣ Auto Commit Example
Configuration
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "5000");

This means:

Every 5 seconds, Kafka commits latest offset automatically.

==================================================================

✔Complete Auto Commit Consumer

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class AutoCommitConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "auto-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        // AUTO COMMIT ENABLED
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "5000");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("order-events"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: "
                        + record.value()
                        + " Offset: "
                        + record.offset());
            }
        }
    }
}

🔥 Problem with Auto Commit

Scenario:

Consumer polls message

Kafka auto-commits offset

Application crashes before processing

Message lost ❌

Why?
Offset committed before processing completed.

============================================================

🔹 6️⃣ Manual Commit Example (Recommended)

✔Configuration

props.put("enable.auto.commit", "false");
Complete Manual Commit Consumer
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ManualCommitConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "manual-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        // MANUAL COMMIT
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("order-events"));

        while (true) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {

                try {
                    System.out.println("Processing: "
                            + record.value()
                            + " Offset: "
                            + record.offset());

                    process(record.value());

                } catch (Exception e) {
                    System.out.println("Error occurred. Not committing offset.");
                    break;
                }
            }

            // Commit only AFTER successful processing
            consumer.commitSync();
        }
    }

    private static void process(String message) {
        System.out.println("Business Logic: " + message);
    }
}

🔥 What Happens in Manual Commit?

Flow:

1. Poll records
2. Process records
3. commitSync()
4. Offset stored in __consumer_offsets

If crash happens before commit:

Kafka re-delivers message

Ensures At-Least-Once delivery

============================================================

7️⃣ commitSync vs commitAsync

Method	Blocking?	Safe?	Use Case
commitSync()	Yes	Very Safe	Critical systems
commitAsync()	No	Faster	High throughput

Example async:

consumer.commitAsync();

8️⃣ What Happens During Crash?

Scenario 1: Crash BEFORE commit

Offset not saved → message reprocessed
✔ No data loss
❗ Possible duplicate

Scenario 2: Crash AFTER commit

Offset saved → message skipped
✔ No duplicate
❗ Risk if commit happened before processing

🔥 Important Concept: auto.offset.reset
props.put("auto.offset.reset", "earliest");

Values:

Value	Meaning
earliest	Read from beginning
latest	Read new messages only
none	Throw error

Used when:

No committed offset exists

📊 Deep Offset Architecture Diagram
Partition Log:
Offset 0
Offset 1
Offset 2
Offset 3
Offset 4

Consumer Group A:
Committed Offset → 2

Next poll → starts from 3

🧠 Advanced Insight

Offset tracking is:

NOT stored in consumer memory permanently

Stored inside Kafka

Partition-specific

Group-specific

Offset is:
👉 Pointer to next message to read
Not pointer to last processed message

===================================================

🎯 Final Summary:

Concept	Meaning

Offset	Unique sequential number per partition
Managed By	Consumer group
Stored In	__consumer_offsets topic
Auto Commit	Easy but risky
Manual Commit	Safe and recommended
Commit After Processing	Ensures at-least-once

🚀 Since We're Designing Event-Driven Systems

For reward engines, batch processing, reprocessing flows:

We should ALWAYS use:

✔Manual commit

✔Idempotent processing

✔Dead letter handling

we can explain:

1️⃣ Consumer Group Rebalancing & Offset Behavior
2️⃣ Exactly-Once Processing with Transactions
3️⃣ Offset Reset & Replay Strategy
4️⃣ How Kafka Stores Offsets Internally (Deep Internals)
5️⃣ Designing Idempotent Consumers

Which one should we go deep into next? 🚀

Explain with complete code example? 

=======================================================

5️⃣ Replication & Fault Tolerance

Each partition has:

Leader

Followers (replicas)

Replication factor example:

If leader fails:

Follower becomes leader


5️⃣ Replication & Fault Tolerance in Kafka with:

✅ Architecture Explanation

✅ Leader & Follower Concept

✅ Replication Factor Example

✅ Complete Producer + Consumer Code

✅ Failure Scenario Simulation

✅ Internal Flow Diagram

Platform reference: Apache Kafka

=========================================================

📘 5️⃣ Replication & Fault Tolerance

Kafka achieves fault tolerance using:

Replication Factor

Leader–Follower model

ISR (In-Sync Replicas)

Automatic leader election

1️⃣ What is Replication?

Replication means:

Copying partition data across multiple brokers.

If:

Replication Factor (RF) = 3

Each partition has:

1 Leader

2 Followers

🔥 Visual Example

Topic: order-events
Partitions: 1
Replication Factor: 3

                Kafka Cluster

Broker 1        Broker 2        Broker 3
---------       ---------       ---------
Partition 0     Partition 0     Partition 0
(Leader)        (Follower)      (Follower)

✔ All writes go to Leader
✔ Followers replicate from Leader
✔ If Leader fails → Follower becomes new Leader

==================================================================

2️⃣ Create Topic with Replication

To test replication, we need multiple brokers running.

Example topic creation:

bin/kafka-topics.sh --create \
--topic order-events \
--bootstrap-server localhost:9092 \
--partitions 1 \
--replication-factor 3

Now partition metadata:

Partition 0:
Leader → Broker 1
Follower → Broker 2
Follower → Broker 3

3️⃣ How Replication Works Internally

Step 1: Producer sends message✔

Producer writes to Leader.

Step 2: Leader appends to log✔

Offset 0
Offset 1
Offset 2
Step 3: Followers replicate

Followers copy data from leader.

Step 4: Acknowledgement✔

If:

acks = "all"

Producer waits until:

All ISR replicas acknowledge.

4️⃣ Complete Producer Code (Safe Configuration)

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ReplicationProducer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        // CRITICAL FOR FAULT TOLERANCE
        props.put("acks", "all"); 
        props.put("retries", 3);
        props.put("min.insync.replicas", 2);

        Producer<String, String> producer =
                new KafkaProducer<>(props);

        ProducerRecord<String, String> record =
                new ProducerRecord<>("order-events",
                        "101",
                        "Order Created");

        producer.send(record, (metadata, exception) -> {

            if (exception == null) {
                System.out.println("Sent to Partition: "
                        + metadata.partition()
                        + " Offset: "
                        + metadata.offset());
            } else {
                exception.printStackTrace();
            }
        });

        producer.close();
    }
}

5️⃣ Consumer Code (Unchanged by Replication)✔

Consumers always read from Leader.

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ReplicationConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "replication-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singleton("order-events"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: "
                        + record.value()
                        + " Offset: "
                        + record.offset());
            }
        }
    }
}


🔥 6️⃣ What Happens If Leader Fails?

Scenario:

Leader = Broker 1
Broker 1 crashes.

Step-by-step:

1️⃣ Controller detects broker failure
2️⃣ New leader elected from ISR
3️⃣ Partition metadata updated
4️⃣ Clients refresh metadata
5️⃣ Processing continues

🔥 Visual Failover Example

Before crash:

Broker 1 → Leader
Broker 2 → Follower
Broker 3 → Follower

After crash:

Broker 2 → Leader
Broker 3 → Follower
Broker 1 → Down

Producer automatically retries.

No data loss if:

acks=all

min.insync.replicas configured properly

7️⃣ Important Configurations for Fault Tolerance
Producer Side
acks=all
retries=3
enable.idempotence=true
Broker Side
replication.factor=3
min.insync.replicas=2
unclean.leader.election.enable=false

8️⃣ What is ISR (In-Sync Replica)?

ISR = List of replicas that are fully caught up.

Only ISR members can become leader.

If replica falls behind → removed from ISR.

9️⃣ What Causes Data Loss?

Data loss happens if:

replication.factor = 1

acks = 0

unclean leader election enabled

min.insync.replicas misconfigured

🔟 Deep Internal Flow
Producer
   ↓
Leader Partition
   ↓
Append to Log
   ↓
Replicate to Followers
   ↓
Wait for ISR ACK
   ↓
High Watermark Updated
   ↓
ACK to Producer

✔Consumer only reads up to High Watermark.

🧠 What Happens During Leader Election?

✔Kafka Controller:

Detects broker failure

Picks new leader from ISR

Updates metadata

Notifies clients

Modern Kafka (KRaft mode):

Uses Raft consensus internally

🎯 Real-World Importance✔

For systems like:

✔Banking

✔Payment processing

✔Reward engines

✔Incentive systems

We must configure:

Replication Factor ≥ 3
acks=all
min.insync.replicas=2

==============================================

To avoid catastrophic data loss.

📊 Summary Table

Concept	Meaning
Leader	Handles read/write
Follower	Replicates data
Replication Factor	Number of copies
ISR	In-sync replicas
Failover	Automatic leader election
acks=all	Wait for all replicas

🚀 Next Advanced Options

we can explain:

1️⃣ ISR Shrink & High Watermark Deep Internals
2️⃣ Exactly-Once + Idempotent Producer
3️⃣ Consumer Rebalancing During Broker Failure
4️⃣ Kafka Multi-Broker Setup (Docker Compose Example)
5️⃣ Simulating Broker Crash Step-by-Step

Which one should we go deep into next? 🚀

Explain with complete example with code?

==================================================================

6️⃣ Consumer Groups

Important concept.

Rules:

One partition assigned to only one consumer in group

Different groups get full copy of data

Great 👍 Let’s go deep into Chapter 6 – Consumer Groups with:

✔ Concept Explanation

✔ Architecture Diagram

✔ Complete Spring Boot Code Example

✔ Real Microservices Example

✔ Rebalancing & Scaling Explanation
===============================================================

6️⃣ Consumer Groups (Complete Deep Dive)

🔥 What is a Consumer Group?

A Consumer Group is a group of consumers working together to read data from Kafka topics.

👉 Kafka guarantees:

One partition is assigned to only one consumer within a group

But:

Different consumer groups receive the full copy of data

🧠 Why Consumer Groups?

Used for:

✔Microservices scaling

✔Parallel processing

✔Load balancing

✔Event-driven systems

✔Fault tolerance

📊 Consumer Group Architecture Diagram
                  Kafka Topic: orders
               --------------------------------
               | Partition 0 | Partition 1 | Partition 2 |
               --------------------------------

Consumer Group: payment-group

        Consumer A  → Partition 0
        Consumer B  → Partition 1
        Consumer C  → Partition 2

--------------------------------------------

Consumer Group: reward-group

        Consumer X  → Partition 0
        Consumer Y  → Partition 1
        Consumer Z  → Partition 2
Important Rules

✔ Inside a group → No duplicate processing
✔ Across groups → Each group gets full copy
✔ If consumers > partitions → Some consumers idle
✔ If partitions > consumers → Some consumers handle multiple partitions

🏗 Real Microservices Example

Let’s use:

Order Service → Produces order event

Payment Service → Consumer Group 1

Reward Service → Consumer Group 2

📦 Step 1: Create Kafka Topic✔

kafka-topics.sh --create \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
  
🚀 Step 2: Producer – Order Service✔

OrderProducer.java
@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String orderId) {
        kafkaTemplate.send("orders", orderId);
        System.out.println("Order sent: " + orderId);
    }
}
💳 Step 3: Payment Service (Consumer Group 1)✔

application.yml
spring:
  kafka:
    consumer:
      group-id: payment-group
      auto-offset-reset: earliest
      bootstrap-servers: localhost:9092
PaymentConsumer.java
@Service
public class PaymentConsumer {

    @KafkaListener(topics = "orders", groupId = "payment-group")
    public void processPayment(String message) {
        System.out.println("Payment processed for: " + message);
    }
}
🎁 Step 4: Reward Service (Consumer Group 2)✔

application.yml
spring:
  kafka:
    consumer:
      group-id: reward-group
      auto-offset-reset: earliest
      bootstrap-servers: localhost:9092
RewardConsumer.java
@Service
public class RewardConsumer {

    @KafkaListener(topics = "orders", groupId = "reward-group")
    public void processReward(String message) {
        System.out.println("Reward processed for: " + message);
    }
}

🔥 What Happens Now?

If producer sends:

Order1
Order2
Order3

Result:

Message	Payment Group	Reward Group
Order1	Processed	Processed
Order2	Processed	Processed
Order3	Processed	Processed

👉 Both groups receive full data.

But inside payment-group:

If 3 consumers running → each gets one partition.

🧠 Partition Assignment Example

Suppose:

Topic: orders
Partitions: 3

Payment Group has:

Consumer A

Consumer B

Assignment:

Consumer A → Partition 0
Consumer B → Partition 1 & 2

⚡ Scaling Example

If we increase Payment Service instances to 3:

Instance 1 → Partition 0
Instance 2 → Partition 1
Instance 3 → Partition 2

Perfect parallel processing 🚀

🔁 What is Rebalancing?✔

Rebalancing happens when:

Consumer joins group

Consumer leaves group

Broker restarts

Kafka reassigns partitions automatically.

Example:

Consumer B crashes:

Before:

A → P0
B → P1
C → P2

After crash:

A → P0
C → P1 & P2

This is automatic fault tolerance.

⚙️ Internal Working of Consumer Group

Consumers join group

Kafka elects a Group Coordinator

Coordinator assigns partitions

Consumers start polling

Offsets stored per group

🧠 Where Are Offsets Stored?

In internal topic:

__consumer_offsets

Each group maintains its own offset.

🔬 Advanced Interview Concepts✔

1️⃣ What if consumers > partitions?

Extra consumers remain idle.

2️⃣ Can two consumers read same partition in same group?

❌ No.

3️⃣ Can two different groups read same partition?

✅ Yes.

4️⃣ How to achieve parallelism?

Increase partitions.

5️⃣ How to ensure ordering?

Ordering guaranteed only within partition.

🏗 Real Production Microservices Flow
Order Service (Producer)
        ↓
     Kafka Topic
        ↓
--------------------------------
| payment-group               |
| reward-group                |
| analytics-group             |
--------------------------------

Each group independently processes events.

🏆 Architect-Level Understanding

Consumer Groups provide:

Horizontal scalability

Fault tolerance

Replay capability

Event-driven architecture support

Decoupled microservices

🔥 If You Want Next Level

I can explain:

Sticky vs Range Partition Assignment

Cooperative Rebalancing

Exactly Once Processing with Groups

Consumer Lag Monitoring

Kafka with Kubernetes scaling

Consumer group internals (protocol level)

Would you like:

👉 Consumer Group + Offset Deep Internal Flow Diagram
👉 Or Full Real Production Architecture with Docker & Kubernetes?
=====================================================================
7️⃣ Kafka Installation & Setup✔

7️⃣ Kafka Installation & Setup (Complete Practical Guide)✔

We’ll cover:

✔ What you need before installing

✔ Install Apache Kafka (with ZooKeeper mode)

✔ Install Kafka in KRaft mode (no ZooKeeper – modern way)

✔ Run Producer & Consumer (CLI)

✔ Docker Setup (Production Style)

✔ Verify Cluster Internals

🔹 1️⃣ Prerequisites

We need:

Java 8 or 11+

Linux / Mac / Windows

4GB RAM minimum

Check Java:

java -version

🔥 2️⃣ Download & Install Apache Kafka (Manual Setup)

Step 1: Download Kafka✔

Go to official website:

👉 https://kafka.apache.org/downloads

Download latest stable binary.✔

Or using command:

wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz

Step 2: Extract✔

tar -xzf kafka_2.13-3.6.1.tgz

cd kafka_2.13-3.6.1

🔹 3️⃣ Start Kafka (ZooKeeper Mode – Traditional)

✔Older Kafka versions require ZooKeeper.

Step 1: Start ZooKeeper✔

bin/zookeeper-server-start.sh config/zookeeper.properties

Step 2: Start Kafka Broker✔

bin/kafka-server-start.sh config/server.properties

Kafka running at:

localhost:9092

🔹 4️⃣ Kafka Modern Setup (KRaft Mode – Recommended)

Kafka 3.x supports KRaft mode (no ZooKeeper).

Step 1: Generate Cluster ID✔

bin/kafka-storage.sh random-uuid

Step 2: Format Storage✔

bin/kafka-storage.sh format \
  -t <generated-uuid> \
  -c config/kraft/server.properties
  
Step 3: Start Kafka✔

bin/kafka-server-start.sh config/kraft/server.properties

Now Kafka runs without ZooKeeper 🚀

🔹 5️⃣ Create Topic

bin/kafka-topics.sh --create \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

Verify:

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

🔹 6️⃣ Run Console Producer

bin/kafka-console-producer.sh \
  --topic orders \
  --bootstrap-server localhost:9092

Type messages:

Order1
Order2
Order3

🔹 7️⃣ Run Console Consumer

bin/kafka-console-consumer.sh \
  --topic orders \
  --from-beginning \
  --bootstrap-server localhost:9092

We will see:

Order1
Order2
Order3

📊 Kafka Installation Architecture Diagram

             +----------------------+
             |      Producer        |
             +----------+-----------+
                        |
                        v
                +---------------+
                |   Kafka       |
                |   Broker      |
                | (Port 9092)   |
                +-------+-------+
                        |
                        v
                +---------------+
                |   Consumer    |
                +---------------+
				
🐳 8️⃣ Kafka Installation Using Docker (Recommended for Dev)

We will use:

Apache Kafka

Apache ZooKeeper

Docker

Step 1: Create docker-compose.yml✔

version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
	  
Step 2: Start Containers✔

docker-compose up -d

Check:

docker ps

Kafka running 🚀

🔍 Verify Kafka Cluster

Check broker metadata:

bin/kafka-broker-api-versions.sh --bootstrap-server localhost:9092

Check consumer groups:

bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

🏗 Production Installation Architecture

In real production:

        +----------------------+
        |   Load Balancer      |
        +----------+-----------+
                   |
    -------------------------------------
    |        Kafka Cluster (3 Nodes)    |
    |-----------------------------------|
    | Broker1 | Broker2 | Broker3      |
    -------------------------------------

Each broker runs on separate server.

Replication factor = 3
Partitions distributed across brokers.

🔐 Production Setup Checklist

✔ Multiple brokers (3+)
✔ Replication factor ≥ 3
✔ Separate disks for logs
✔ Monitoring via Prometheus + Grafana
✔ SSL + SASL authentication
✔ Proper retention policy

🎯 Interview Questions from This Chapter

✔Difference between ZooKeeper and KRaft mode?

✔What happens if broker crashes?

✔Where are messages stored physically?

✔What is advertised.listeners?

✔How to scale Kafka cluster?

✔Why replication factor cannot exceed brokers?
============================================================

🚀 Next Advanced Chapters

We can continue with:

8️⃣ Kafka Message Retention & Log Compaction
9️⃣ Kafka Security (SSL, SASL, ACL)

🔟 Kafka Performance Tuning

1️⃣1️⃣ Kafka Monitoring & Metrics
1️⃣2️⃣ Kafka with Kubernetes (Helm Setup)

If We're preparing architect-level knowledge, I suggest next:

👉 Kafka Cluster Setup with 3 Brokers (Full Production Simulation)
👉 OR Kafka Security + SSL + SASL Complete Guide

Which one would you like next?

8️⃣ Basic Kafka Commands
8️⃣ Basic Kafka Commands (Complete Practical Guide)

We’ll cover most-used real-world commands for managing
Apache Kafka clusters.

This chapter is very important for interviews + production debugging.
=============================================================================

📌 1️⃣ Start Kafka Server

🟢 Start ZooKeeper (Old Mode)
bin/zookeeper-server-start.sh config/zookeeper.properties
🟢 Start Kafka Broker
bin/kafka-server-start.sh config/server.properties

📌 2️⃣ Stop Kafka Server

bin/kafka-server-stop.sh

Stop ZooKeeper:

bin/zookeeper-server-stop.sh

📌 3️⃣ Create Topic
bin/kafka-topics.sh --create \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
Parameters Explained:

--topic → Topic name

--partitions → Parallelism level

--replication-factor → Fault tolerance copies

📌 4️⃣ List Topics

bin/kafka-topics.sh --list \
  --bootstrap-server localhost:9092
  
📌 5️⃣ Describe Topic (Very Important)

bin/kafka-topics.sh --describe \
  --topic orders \
  --bootstrap-server localhost:9092

Output shows:

Partition count

Leader

Replicas

In-sync replicas (ISR)

Example:

Topic: orders
Partition: 0  Leader: 1  Replicas: 1  ISR: 1
Partition: 1  Leader: 1  Replicas: 1  ISR: 1
Partition: 2  Leader: 1  Replicas: 1  ISR: 1

📌 6️⃣ Delete Topic

bin/kafka-topics.sh --delete \
  --topic orders \
  --bootstrap-server localhost:9092

⚠ Make sure deletion is enabled in server.properties:

delete.topic.enable=true

📌 7️⃣ Produce Messages (Console Producer)

bin/kafka-console-producer.sh \
  --topic orders \
  --bootstrap-server localhost:9092

Then type:

Order1
Order2
Order3

📌 8️⃣ Consume Messages (Console Consumer)

Read from latest:
bin/kafka-console-consumer.sh \
  --topic orders \
  --bootstrap-server localhost:9092
Read from beginning:
bin/kafka-console-consumer.sh \
  --topic orders \
  --from-beginning \
  --bootstrap-server localhost:9092
  
📌 9️⃣ Create Topic with Custom Config

bin/kafka-topics.sh --create \
  --topic payments \
  --bootstrap-server localhost:9092 \
  --partitions 2 \
  --replication-factor 1 \
  --config retention.ms=60000

This keeps messages for 1 minute.

📌 🔟 View Consumer Groups

bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --list
  
📌 1️⃣1️⃣ Describe Consumer Group (VERY IMPORTANT)

bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --group payment-group

Output:

Current offset

Log end offset

Lag

Assigned partition

Example:

GROUP            TOPIC       PARTITION       CURRENT-OFFSET        LOG-END-OFFSET          LAG
payment-group   orders       0                 5                      10                     5

👉 Lag = 5 (Consumer behind by 5 messages)

📌 1️⃣2️⃣ Reset Consumer Offset

Reset to earliest:
bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group payment-group \
  --topic orders \
  --reset-offsets \
  --to-earliest \
  --execute
Reset to latest:
--to-latest

📌 1️⃣3️⃣ Check Broker API Versions

bin/kafka-broker-api-versions.sh \
  --bootstrap-server localhost:9092

Useful for debugging compatibility issues.

📌 1️⃣4️⃣ Check Partition Reassignment

bin/kafka-reassign-partitions.sh

Used for:

Moving partitions between brokers

Rebalancing cluster

📌 1️⃣5️⃣ Verify Cluster Metadata

bin/kafka-topics.sh --describe --bootstrap-server localhost:9092

Shows all topics and partition distribution.

===================================================

📊 Basic Kafka Command Flow Diagram
Producer → Topic → Partition → Broker → Consumer Group

Commands help manage:

Topics

Brokers

Consumer groups

Offsets

Retention

Replication

============================================

🧠 Architect-Level Important Commands
Use Case	Command
Check Lag	kafka-consumer-groups.sh --describe
Reset Offset	--reset-offsets
Topic Retention	--config retention.ms
Partition Info	--describe
Rebalance Partitions	kafka-reassign-partitions.sh
=================================================
🎯 Most Asked Interview Commands

How to check consumer lag?

How to reset offsets?

How to increase partitions?

How to check replication status?

How to move partitions between brokers?

How to check ISR list?

🚀 What Next?

We can continue with:

9️⃣ Kafka Retention & Log Compaction

🔟 Kafka Security (SSL, SASL, ACL)

1️⃣1️⃣ Kafka Monitoring & Lag Alerting

1️⃣2️⃣ Kafka Performance Tuning

We're preparing for Architect / Production-level interviews, I recommend:

👉 Kafka Consumer Lag Monitoring Deep Dive
👉 OR Kafka Security End-to-End Setup
=================================================
Which one should we go next?

9️⃣ ✔Producer Basics (Configuration)

10 ✔Consumer Basics (Configuration)

9️⃣ Producer Basics (Configuration)

Kafka Producer is responsible for:

Sending messages to topic

Choosing partition

Handling retries

Ensuring durability

Managing batching & compression

🔥 Producer Architecture

Application
    ↓
KafkaProducer API
    ↓
Serializer
    ↓
Partitioner
    ↓
Broker (Leader Partition)

📌 Important Producer Configurations

1️⃣ bootstrap.servers
bootstrap.servers=localhost:9092

List of brokers to connect initially.

2️⃣ key.serializer & value.serializer
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer

Used to convert object → byte array.

3️⃣ acks (VERY IMPORTANT – Interview Favorite)

Controls durability.

acks=0
acks=1
acks=all
Value	Meaning	Durability
0	No acknowledgement	❌ Unsafe
1	Leader confirms	⚠ Medium
all	Leader + replicas confirm	✅ Strong

For production:

acks=all

4️⃣ retries
retries=3

Retries if send fails.

5️⃣ batch.size
batch.size=16384

Messages are batched before sending.

Bigger batch → Better throughput.

6️⃣ linger.ms
linger.ms=5

Wait time before sending batch.

Improves batching efficiency.

7️⃣ compression.type
compression.type=snappy

Options:

none

gzip

snappy

lz4

zstd

Improves network performance.

8️⃣ enable.idempotence (Exactly Once)
enable.idempotence=true

Prevents duplicate messages during retries.

🔥 Complete Producer Example (Java)
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class OrderProducer {

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", 
                  "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", 
                  "org.apache.kafka.common.serialization.StringSerializer");

        props.put("acks", "all");
        props.put("retries", 3);
        props.put("linger.ms", 5);
        props.put("batch.size", 16384);
        props.put("compression.type", "snappy");
        props.put("enable.idempotence", "true");

        KafkaProducer<String, String> producer =
                new KafkaProducer<>(props);

        ProducerRecord<String, String> record =
                new ProducerRecord<>("orders", "order1", "Order Created");

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Sent to partition: "
                        + metadata.partition());
            }
        });

        producer.close();
    }
}
==============================================
🔟 Consumer Basics (Configuration)✔

Kafka Consumer is responsible for:

Polling messages

Managing offsets

Joining consumer groups

Handling rebalancing

🔥 Consumer Architecture
Broker
   ↓
Consumer Group
   ↓
Partition Assignment
   ↓
poll()
   ↓
Offset Commit

📌 Important Consumer Configurations
1️⃣ bootstrap.servers
bootstrap.servers=localhost:9092

2️⃣ group.id (VERY IMPORTANT)
group.id=payment-group

Consumers with same group ID share partitions.

3️⃣ key.deserializer & value.deserializer
key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

Converts bytes → object.

4️⃣ auto.offset.reset
auto.offset.reset=earliest

Options:

earliest → Read from beginning

latest → Read new messages

none → Throw exception

5️⃣ enable.auto.commit
enable.auto.commit=true

If true → Kafka auto commits offset every 5 sec.

6️⃣ auto.commit.interval.ms
auto.commit.interval.ms=5000

Commit frequency.

7️⃣ max.poll.records
max.poll.records=500

How many records per poll.

8️⃣ session.timeout.ms
session.timeout.ms=10000

If consumer doesn’t send heartbeat → removed from group.

🔥 Complete Consumer Example (Manual Commit)

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class PaymentConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "payment-group");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("orders"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: "
                        + record.value());
            }

            consumer.commitSync(); // Manual commit
        }
    }
}
===================================================
⚖ Producer vs Consumer Configuration Comparison
Feature	Producer	Consumer
Reliability	acks	manual commit
Performance	batch.size	max.poll.records
Duplicate Prevention	idempotence	commit control
Partition Control	partitioner	group.id
Fault Handling	retries	session timeout
🎯 Most Asked Interview Questions
Producer

Difference between acks=1 and acks=all?

What is idempotent producer?

How batching improves performance?

How Kafka ensures ordering?

Consumer

What causes consumer rebalance?

Difference between auto and manual commit?

What happens if consumer crashes?

How to avoid duplicate processing?

🚀 Production Best Practices
Producer

✔ acks=all
✔ enable.idempotence=true
✔ retries=Integer.MAX_VALUE
✔ compression enabled

Consumer

✔ disable auto commit
✔ use manual commit
✔ handle retry logic
✔ monitor consumer lag

================================================

🔥 Next Advanced Topics

We can now move to:

1️⃣1️⃣ Kafka Delivery Semantics (At Most Once, At Least Once, Exactly Once)
1️⃣2️⃣ Kafka Transactions
1️⃣3️⃣ Kafka Performance Tuning
1️⃣4️⃣ Kafka Security (SSL + SASL + ACL)
1️⃣5️⃣ Kafka Monitoring & Lag Deep Dive

If We’re preparing for Senior/Architect-level Kafka interviews, I suggest:

👉 Delivery Semantics + Exactly Once Processing (Critical Topic)

Shall we go there next?

Explain with complete code example and diagram? 11️⃣️ Message Ordering

Ordering guaranteed within partition

Not across partitions

Use key for consistent ordering

All messages with same key → same partition.

=====================================================
Excellent 🔥
Now we are entering one of the most important Kafka concepts for system design interviews in
Apache Kafka

1️⃣1️⃣ Message Ordering in Kafka

🧠 Core Rules

✔ Ordering is guaranteed within a partition
❌ Ordering is NOT guaranteed across partitions
✔ Messages with same key always go to same partition
✔ Kafka preserves order inside that partition

🔥 Why Ordering Matters?

Critical for:

Banking transactions

Order lifecycle updates

Inventory updates

Event sourcing systems

Payment processing

📊 Ordering Architecture Diagram
Case 1️⃣ — No Key Used (Ordering Not Guaranteed)
Topic: orders (3 partitions)

Message1 → Partition 0
Message2 → Partition 2
Message3 → Partition 1
Message4 → Partition 0

Across partitions → order NOT guaranteed.

Case 2️⃣ — Key Used (Ordering Guaranteed Per Key)
Topic: orders (3 partitions)

Key = order123

OrderCreated   → Partition 1
OrderPaid      → Partition 1
OrderShipped   → Partition 1
OrderDelivered → Partition 1

All events for same order go to same partition.

✔ Ordering maintained.

🔎 How Kafka Decides Partition?

If key is present:

partition = hash(key) % number_of_partitions

Same key → Same hash → Same partition.

🏗 Real Microservices Example

Imagine:

Order Service → Produces events
Payment Service → Consumes
Shipping Service → Consumes

If we do NOT use key:

OrderCreated(order1)
OrderPaid(order1)
OrderShipped(order1)

They may land in different partitions → out of order.

If we use key = orderId:

All events go to same partition → correct sequence.

🔥 Complete Producer Example (With Key)
import org.apache.kafka.clients.producer.*;
import java.util.Properties;

public class OrderProducer {

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("acks", "all");
        props.put("enable.idempotence", "true");

        KafkaProducer<String, String> producer =
                new KafkaProducer<>(props);

        String orderId = "order123";

        producer.send(new ProducerRecord<>("orders",
                orderId, "OrderCreated"));

        producer.send(new ProducerRecord<>("orders",
                orderId, "OrderPaid"));

        producer.send(new ProducerRecord<>("orders",
                orderId, "OrderShipped"));

        producer.send(new ProducerRecord<>("orders",
                orderId, "OrderDelivered"));

        producer.close();
    }
}

Because key = orderId
All events go to same partition.

=====================================================================

🔥 Consumer Example (Ordering Preserved)
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class OrderConsumer {

    public static void main(String[] args) {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-group");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("orders"));

        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println(
                        "Partition: " + record.partition()
                        + " | Offset: " + record.offset()
                        + " | Event: " + record.value());
            }
        }
    }
}

Output will be:

Partition: 1 | Offset: 0 | OrderCreated
Partition: 1 | Offset: 1 | OrderPaid
Partition: 1 | Offset: 2 | OrderShipped
Partition: 1 | Offset: 3 | OrderDelivered

Ordering preserved 🚀

⚠ What Happens Without Key?
producer.send(new ProducerRecord<>("orders", null, "OrderCreated"));

Kafka distributes messages in round-robin fashion.

Result:

Partition 0 → OrderCreated
Partition 2 → OrderPaid
Partition 1 → OrderShipped

Now order is broken ❌

🧠 Important Architect-Level Scenarios
Scenario 
1️⃣ Multiple Orders✔

Key = orderId

order1 → Partition 0
order2 → Partition 2
order3 → Partition 1

Each order has correct internal ordering.
But cross-order ordering not guaranteed (and not needed).

Scenario 2️⃣ Increase Partitions✔

If partitions increase:

Hash result may change.

⚠ Existing ordering may break for new messages.

Best Practice:

Decide partition count early

Avoid changing partitions in production

⚡ Advanced: How Ordering Can Break?

Even within partition if:

retries > 0

idempotence disabled

max.in.flight.requests.per.connection > 1

Best production config:

acks=all
enable.idempotence=true
max.in.flight.requests.per.connection=1

Ensures strict ordering + no duplicates.

📊 Visual Internal Flow
Producer
   ↓
Partitioner (hash(key))
   ↓
Partition 1
   ↓
Offset 0
Offset 1
Offset 2
Offset 3
   ↓
Consumer (Sequential Read)
=========================================
🎯 Interview Questions

Is Kafka globally ordered?

How to guarantee ordering?

Can we have ordering across partitions?

What happens if partitions increase?

Does replication affect ordering?

What breaks ordering?

🏆 Real-World Best Practices

✔ Always use meaningful key (orderId, userId)
✔ Enable idempotence
✔ Keep partition count stable
✔ Avoid high in-flight requests

🚀 Next Advanced Topics

We can now move to:

1️⃣2️⃣ Delivery Semantics (At-most-once, At-least-once, Exactly-once)
1️⃣3️⃣ Kafka Transactions
1️⃣4️⃣ Log Compaction
1️⃣5️⃣ Kafka Performance Tuning

👉 Delivery Semantics + Exactly Once Processing is the next critical topic.

Shall we go there next? 🔥

Explain with Spring boot, java, microservices, posgresql, kafka with code example for below use cases? 

12️⃣Kafka Use Case Examples

✔Order processing

✔Payment processing

✔Notification service

✔Reward engine

✔Log streaming

Excellent 🔥 This is a real-world event-driven microservices architecture question.

We’ll build a complete example using:

☕ Spring Boot

☕ Java

🐘 PostgreSQL

📨 Apache Kafka

🏗 Microservices Architecture

🏗 System Architecture Overview
🎯 Use Cases Covered

1️⃣ Order Processing
2️⃣ Payment Processing
3️⃣ Notification Service
4️⃣ Reward Engine
5️⃣ Log Streaming
=============================================

📊 Architecture Diagram
                ┌───────────────────┐
                │   Order Service   │
                │ (PostgreSQL)      │
                └─────────┬─────────┘
                          │  (OrderCreated Event)
                          ▼
                    ┌───────────────┐
                    │   Kafka       │
                    │  Topic: orders│
                    └───────────────┘
                          │
      ┌───────────────────┼───────────────────┐
      ▼                   ▼                   ▼
┌─────────────┐   ┌─────────────┐     ┌─────────────┐
│ Payment Svc │   │ Reward Svc  │     │ Notification│
│ (Postgres)  │   │ (Postgres)  │     │   Service   │
└─────────────┘   └─────────────┘     └─────────────┘
                          │
                          ▼
                   Topic: logs
                          ▼
                  Log Streaming Service
				  
🧩 Step 1️⃣ — Order Processing Service
📌 Responsibilities

Save order in PostgreSQL

Publish OrderCreated event

pom.xml Dependencies
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
 <groupId>org.springframework.kafka</groupId>
 <artifactId>spring-kafka</artifactId>
</dependency>

<dependency>
 <groupId>org.postgresql</groupId>
 <artifactId>postgresql</artifactId>
</dependency>

<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orderdb
    username: postgres
    password: postgres

  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
	  
✔Order Entity
@Entity
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private String orderId;
    private Double amount;
    private String status;
}

✔Order Producer
@Service
public class OrderEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        kafkaTemplate.send("orders", order.getOrderId(), order);
    }
}

✔Order Controller
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private OrderEventProducer producer;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setStatus("CREATED");
        Order saved = repo.save(order);
        producer.publishOrderCreated(saved);
        return saved;
    }
}

💳 Step 2️⃣ — Payment Processing Service✔
application.yml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: payment-group
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
	  
✔Payment Consumer
@Service
public class PaymentConsumer {

    @KafkaListener(topics = "orders", groupId = "payment-group")
    public void processPayment(Order order) {

        System.out.println("Processing payment for: " + order.getOrderId());

        // Save payment in DB
        // Update payment status
    }
}

🎁 Step 3️⃣ — Reward Engine Service✔
@Service
public class RewardConsumer {

    @KafkaListener(topics = "orders", groupId = "reward-group")
    public void calculateReward(Order order) {

        double rewardPoints = order.getAmount() * 0.1;

        System.out.println("Reward Points: " + rewardPoints);

        // Save reward to PostgreSQL
    }
}

📩 Step 4️⃣ — Notification Service✔
@Service
public class NotificationConsumer {

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void sendNotification(Order order) {

        System.out.println("Sending email for order: " + order.getOrderId());

        // Integrate with email service / SMS
    }
}

📊 Step 5️⃣ — Log Streaming Service✔

All services can publish logs to Kafka topic logs.

Example:

kafkaTemplate.send("logs", "Payment processed for order123");

Log Consumer:

@KafkaListener(topics = "logs", groupId = "log-group")
public void consumeLogs(String message) {
    System.out.println("LOG: " + message);
}
=====================================================
🔄 End-to-End Event Flow✔

User → Order Service
        ↓
    Save in PostgreSQL
        ↓
   Publish OrderCreated Event
        ↓
       Kafka
        ↓
---------------------------------------
| Payment Service                    |
| Reward Engine                      |
| Notification Service               |
---------------------------------------
🧠 Important Production Concepts

✔ Use orderId as Kafka key (ensures ordering)
✔ Enable idempotent producer
✔ Manual offset commit in consumer
✔ Use retry + DLQ for failed messages
✔ Monitor consumer lag

🔐 Production Improvements

✔Add Transactional Outbox Pattern

✔Use Kafka Transactions

✔Add Dead Letter Topic

✔Add Retry Topic

✔Enable SSL + SASL

🎯 Real Interview Questions From This Architecture

✔How do you avoid dual-write problem?

✔How to ensure exactly-once payment processing?

✔What if Payment service crashes?

✔How to handle failed reward calculation?

✔How to maintain consistency between DB and Kafka?

🚀 If We Want Next Level

We can now build:

1️⃣ Complete Docker Compose Setup (Kafka + 4 Microservices + PostgreSQL)
2️⃣ Outbox Pattern Implementation
3️⃣ Exactly Once Processing Example
4️⃣ Saga Pattern for Order-Payment Flow
5️⃣ Retry + Dead Letter Queue Implementation
=====================================================================
Since We're going deep into Kafka architecture,
I strongly recommend next:

👉 Outbox Pattern + Exactly Once Processing (Production Grade Design)

Shall we go there next? 🔥
=====================================================================
✔Q.Use CQRS, SAGA, Even sourcing , Database per service patterns? Add Transactional Outbox Pattern

✔Use Kafka Transactions

✔Add Dead Letter Topic

✔Add Retry Topic

✔Enable SSL + SASL

Now we are moving into enterprise-grade, architect-level distributed system design using:

📨 Spring Boot

📨 Apache Kafka

🐘 PostgreSQL

✔Microservices

✔CQRS

✔Saga

✔Event Sourcing

✔Transactional Outbox

Kafka Transactions

✔Retry + DLQ

✔SSL + SASL Security

This is a real production architecture blueprint.

🏗 1️⃣ High-Level Architecture (All Patterns Combined)
                 ┌─────────────────────────┐
                 │     API Gateway         │
                 └────────────┬────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   Order Service   │  (Command Model)
                    │  DB: order_db     │
                    │  + Outbox Table   │
                    └─────────┬─────────┘
                              │
                              ▼
                         Kafka (SSL + SASL)
                              │
   ┌───────────────┬──────────┼───────────┬───────────────┐
   ▼               ▼          ▼           ▼
Payment Svc     Reward Svc   Notify Svc  Log Svc
DB: payment_db  DB: reward_db  (DB per service)

                      ▼
                Read Model Service
                (CQRS Query Side)
				
2️⃣ Database Per Service Pattern

✔Each microservice has its own DB:

Service	Database
Order	order_db
Payment	payment_db
Reward	reward_db

✔Prevents tight coupling.

3️⃣ CQRS Pattern

Separate:

Command model → writes (Order Service)

Query model → reads (Read Service)

Command Example (Order Creation)

@Transactional
public void createOrder(CreateOrderCommand command) {
    Order order = new Order(...);
    orderRepository.save(order);

    outboxRepository.save(
        OutboxEvent.of("OrderCreated", order)
    );
}

Query Side

@GetMapping("/orders/{id}")
public OrderView getOrder(@PathVariable String id) {
    return orderViewRepository.findById(id).get();
}

Query DB is updated via Kafka events.

4️⃣ Event Sourcing✔

Instead of saving final state, store events:

Order Events Table:

id	event_type	payload
1	OrderCreated	{...}
2	PaymentCompleted	{...}
3	OrderShipped	{...}

Rebuild state by replaying events.

public Order rebuild(List<OrderEvent> events) {
    Order order = new Order();
    for (OrderEvent e : events) {
        order.apply(e);
    }
    return order;
}
==============================================
5️⃣ Transactional Outbox Pattern

Solves dual-write problem.

Outbox Table
CREATE TABLE outbox_event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255),
    payload JSONB,
    status VARCHAR(50),
    created_at TIMESTAMP
);
Save Order + Outbox in Same Transaction
@Transactional
public void createOrder(Order order) {
    orderRepository.save(order);

    OutboxEvent event = new OutboxEvent(
        UUID.randomUUID(),
        "OrderCreated",
        objectMapper.writeValueAsString(order),
        "NEW"
    );

    outboxRepository.save(event);
}
Outbox Publisher (Kafka Producer)
@Scheduled(fixedDelay = 5000)
public void publishOutboxEvents() {

    List<OutboxEvent> events =
        outboxRepository.findByStatus("NEW");

    for (OutboxEvent event : events) {

        kafkaTemplate.send("orders",
                event.getId().toString(),
                event.getPayload());

        event.setStatus("SENT");
        outboxRepository.save(event);
    }
}

Now DB + Kafka consistency guaranteed ✅

================================================

6️⃣ Kafka Transactions (Exactly Once)

Enable transactional producer:

spring.kafka.producer.transaction-id-prefix=tx-
spring.kafka.producer.acks=all
spring.kafka.producer.properties.enable.idempotence=true
Producer Code
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void sendTransactionally(String key, String value) {

    kafkaTemplate.executeInTransaction(operations -> {
        operations.send("orders", key, value);
        operations.send("logs", key, "Order logged");
        return true;
    });
}

If one fails → entire transaction rolls back.

================================================

7️⃣ SAGA Pattern (Orchestration Based)

Order flow:

OrderCreated

PaymentRequested

PaymentCompleted

RewardCalculated

OrderCompleted

Saga Orchestrator
@KafkaListener(topics = "orders")
public void handleOrder(OrderEvent event) {

    if (event.getType().equals("OrderCreated")) {
        kafkaTemplate.send("payment-request", event);
    }

    if (event.getType().equals("PaymentFailed")) {
        kafkaTemplate.send("order-cancel", event);
    }
}

Compensation example:

if(paymentFailed) {
   cancelOrder();
}

=====================================================
8️⃣ Retry Topic Pattern

If processing fails:

@KafkaListener(topics = "orders")
public void consume(Order order) {
    try {
        process(order);
    } catch(Exception e) {
        kafkaTemplate.send("orders-retry", order);
    }
}

===========================================================

9️⃣ Dead Letter Topic (DLT)

After retries exhausted:

@KafkaListener(topics = "orders-retry")
public void retry(Order order) {
    try {
        process(order);
    } catch(Exception e) {
        kafkaTemplate.send("orders-dlt", order);
    }
}

DLT consumer:

@KafkaListener(topics = "orders-dlt")
public void deadLetterHandler(Order order) {
    log.error("Failed permanently: {}", order);
}

=================================================

🔐 1️⃣0️⃣ Enable SSL + SASL Security

In Kafka broker:

listeners=SASL_SSL://:9093
security.inter.broker.protocol=SASL_SSL
sasl.mechanism.inter.broker.protocol=PLAIN

Client config:

spring.kafka.properties.security.protocol=SASL_SSL
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
 username="admin" \
 password="admin-secret";

Now communication encrypted + authenticated 🔐

🏆 Full Enterprise Flow
User → Order Service
       → PostgreSQL (order_db)
       → Outbox Table
       → Kafka Transaction
       → Payment Service
       → Reward Service
       → Notification Service
       → CQRS Read Model
	   
==================================================	   
🎯 Interview-Level Questions This Covers

✔How to avoid dual write?

✔How to implement distributed transaction?

✔How to ensure exactly-once?

✔How to design microservices with data isolation?

✔How to implement Saga?

✔Difference between choreography vs orchestration?

✔How to handle poison messages?

✔How to secure Kafka cluster?

🚀 If We Want Ultimate Level

Next we can build:

1️⃣ Full Docker Compose Enterprise Setup
2️⃣ Kubernetes Deployment with Helm
3️⃣ Observability (Prometheus + Grafana)
4️⃣ Kafka Performance Tuning
5️⃣ Real Banking-Grade Architecture

====================================================