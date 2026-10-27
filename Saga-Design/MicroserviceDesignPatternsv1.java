
Microservice Design Patterns:
=============================

1.1 Database per Microservice

Each microservice has its own database to ensure independence, enabling independent development,deployment, and scaling. This encapsulates domain data and supports polyglot persistence, allowing optimized storage choices (e.g., NoSQL for product catalogs, relational for
orders

// ProductServiceApplication.java
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class ProductServiceApplication {
public static void main(String[] args) {
SpringApplication.run(ProductServiceApplication.class , args);
 }
 }

 // Product.java
com.example.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
@Id
private String id;
private String name;
private double price;
// Getters and setters
}
Listing 1: Database per Microservice Example in Spring Boot
// ProductRepository.java
interface ProductRepository extends MongoRepository <Product , String > {}

// ProductController.java
@RestController
@RequestMapping("/products")
class ProductController {
private final ProductRepository repository;

public ProductController(ProductRepository repository) {
this.repository = repository;
 }

@GetMapping("/{id}")
public Product getProduct(@PathVariable String id) {
return repository.findById(id).orElseThrow(() -> new
RuntimeException("Product not found"));
 }


@PostMapping
public Product createProduct(@RequestBody Product product) {
return repository.save(product);
}
 }
Listing 2: Database per Microservice Example in Spring Boot (Continued)
This code shows a Spring Boot microservice using MongoDB for product data, isolated from
other services’ databases

=================================================================================

1.2 CQRS (Command Query Responsibility Segregation)
CQRS separates read and write operations into different databases to optimize performance for
read-intensive or write-heavy applications, using event-driven architecture for synchronization.

// OrderCommandController.java
package com.example.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {
@Autowired
private KafkaTemplate <String , String > kafkaTemplate;
@Autowired
private OrderRepository orderRepository;

@PostMapping
public Order createOrder(@RequestBody Order order) {
Order savedOrder = orderRepository.save(order);
kafkaTemplate.send("order -events", "OrderCreated:" +
savedOrder.getId());
return savedOrder;
 }
 }
 
Listing 3: CQRS Example in Spring Boot

// OrderQueryController.java
@RestController
@RequestMapping("/orders/query")
class OrderQueryController {
@Autowired
private OrderReadRepository readRepository;
7
@GetMapping("/{id}")
public OrderReadModel getOrder(@PathVariable String id) {
return readRepository.findById(id).orElseThrow(() -> new
RuntimeException("Order not found"));
}
}

// OrderEventListener.java
@Component

class OrderEventListener {
@Autowired
private OrderReadRepository readRepository;

@KafkaListener(topics = "order -events")
public void handleEvent(String event) {
if (event.startsWith("OrderCreated:")) {
String orderId = event.split(":")[1];
// Update read database (e.g., MongoDB) with order data
readRepository.save(new OrderReadModel(orderId));
 }
 }
 
 
Listing 4: CQRS Example in Spring Boot (Continued)

This code demonstrates CQRS with a write database publishing events to Kafka, which updates
a read database.

Core Idea

Writes and Reads are different problems — stop solving them with the same model.

Traditional (CRUD):                    CQRS:

          ┌─────────┐                        ┌─────────┐
 Read ───►│         │                Read ──►│  Query  │──► Read DB
          │ Single  │                        │  Model  │    (optimized)
Write ───►│   DB    │                        └─────────┘
          │         │
          └─────────┘                        ┌─────────┐
                                   Write ───►│ Command │──► Write DB
                                             │  Model  │    (normalized)
                                             └─────────┘
Building Blocks
1. Command (Write Side)

Represents intent to change state
Validated, processed, persisted to write DB
Returns nothing (or just an ack)
// Command — intent
public class PlaceOrderCommand {
    private final String customerId;
    private final List<OrderItem> items;
    private final String shippingAddress;

    public PlaceOrderCommand(String customerId,
                             List<OrderItem> items,
                             String shippingAddress) {
        this.customerId = customerId;
        this.items = items;
        this.shippingAddress = shippingAddress;
    }
    // getters...
}

// Command Handler — processes the intent
@Service
public class PlaceOrderCommandHandler {

    private final OrderWriteRepository writeRepo;
    private final EventPublisher eventPublisher;

    public void handle(PlaceOrderCommand command) {
        // 1. Validate
        if (command.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // 2. Apply business logic
        Order order = Order.create(
            command.getCustomerId(),
            command.getItems(),
            command.getShippingAddress()
        );

        // 3. Persist to write DB
        writeRepo.save(order);

        // 4. Publish event for read side sync
        eventPublisher.publish(new OrderPlacedEvent(order.getId(), order));
    }
}

2. Query (Read Side)

Represents data retrieval — no side effects
Reads from read DB (denormalized, pre-aggregated)
Optimized for the exact shape the UI needs

// Query — what you want
public class GetOrderSummaryQuery {
    private final String customerId;

    public GetOrderSummaryQuery(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }
}

// Read Model — flat, UI-friendly, denormalized
public class OrderSummaryView {
    private String orderId;
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private LocalDateTime placedAt;
    // No joins needed — all data pre-aggregated
}

// Query Handler — just fetch, no logic
@Service
public class GetOrderSummaryQueryHandler {

    private final OrderReadRepository readRepo;

    public List<OrderSummaryView> handle(GetOrderSummaryQuery query) {
        return readRepo.findByCustomerId(query.getCustomerId());
    }
}


3. Event Synchronization (Write → Read)
// Event published after write
public class OrderPlacedEvent {
    private final String orderId;
    private final Order order;
    private final LocalDateTime occurredAt;

    public OrderPlacedEvent(String orderId, Order order) {
        this.orderId = orderId;
        this.order = order;
        this.occurredAt = LocalDateTime.now();
    }
    // getters...
}

// Event listener updates the read DB
@Service
public class OrderEventListener {

    private final OrderReadRepository readRepo;
    private final CustomerReadRepository customerRepo;

    @EventListener   // or @KafkaListener / @RabbitListener
    public void on(OrderPlacedEvent event) {
        Order order = event.getOrder();

        // Denormalize — join data now, not at query time
        String customerName = customerRepo
                .findNameById(order.getCustomerId());

        OrderSummaryView view = new OrderSummaryView();
        view.setOrderId(order.getId());
        view.setCustomerName(customerName);        // ← pre-joined
        view.setStatus(order.getStatus().name());
        view.setTotalAmount(order.getTotalAmount());
        view.setShippingAddress(order.getShippingAddress());
        view.setPlacedAt(event.getOccurredAt());

        // Persist to read DB (MongoDB, Redis, Elasticsearch, etc.)
        readRepo.save(view);
    }
}


Full FlowClient
    │
    ├──── POST /orders ──────────────────────────────────────────────┐
    │                                                                 ▼
    │                                                    PlaceOrderCommand
    │                                                         │
    │                                                    Validate + Save
    │                                                         │
    │                                                    Write DB (MySQL)
    │                                                         │
    │                                                    OrderPlacedEvent
    │                                                         │
    │                                                    Event Bus (Kafka)
    │                                                         │
    │                                                    OrderEventListener
    │                                                         │
    │                                                    Read DB (MongoDB)
    │                                                    (denormalized view)
    │
    └──── GET /orders?customerId=123 ──► QueryHandler ──► Read DB ──► Response
                                                          (fast, no joins)
														  
														  

Database       Choices       per         Side
WritePostgreSQL / MySQLACID, constraints, normalization
ReadMongoDBFlexible denormalized documents
ReadRedisUltra-fast, cache-like readsReadElasticsearchFull-text search, complex filtersReadCassandraHigh-volume, time-series reads

Eventual Consistency — The Tradeoff

User places order         Write DB updated       Read DB updated
      │                         │                      │
      ▼                         ▼                      ▼
   t=0ms ─────────────────── t=5ms ──── Kafka ──── t=50ms
                                                       ↑
                                               small lag here
											   
A read immediately after a write may return stale data. This is acceptable in most cases — design your UI to handle it (optimistic updates, loading states).											   
When to Use CQRS
✅ Read/write loads are asymmetric (e.g. 95% reads, 5% writes)
✅ Read models need different shapes than write models (dashboards, reports)
✅ You need independent scaling of read and write services
✅ Already using Event Sourcing (natural fit)
✅ Complex domains with rich business logic on writes

=============================================================================

1.3 Event Sourcing

Event Sourcing stores all state changes as a sequence of events in an event store, allowing state
reconstruction and recovery by replaying events.

// EventStore.java
package com.example.eventstore;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class EventStore {
private Map<String , List<Event >> events = new HashMap <>();

public void saveEvent(String aggregateId , Event event) {
events.computeIfAbsent(aggregateId , k -> new
ArrayList <>()).add(event);
 }

public List<Event > getEvents(String aggregateId) {
return events.getOrDefault(aggregateId , new ArrayList <>());
 }
 }

// OrderService.java
@Service
class OrderService {
@Autowired
private EventStore eventStore;

public void createOrder(String orderId , String item) {
Event event = new Event("OrderCreated", Map.of("item", item));
eventStore.saveEvent(orderId , event);
 }
 }
 
Listing 5: Event Sourcing Example in Spring Boot

public Order reconstructOrder(String orderId) {

Order order = new Order(orderId);
List<Event > events = eventStore.getEvents(orderId);
 for (Event event : events) {
 if ("OrderCreated".equals(event.getType())) {
 order.addItem((String) event.getData().get("item"));
}
}
return order;
}
}

// Event.java and Order.java
class Event {
private String type;
private Map<String , Object > data;
public Event(String type, Map<String , Object > data) {
this.type = type;
this.data = data;
}
// Getters
}

class Order {
private String id;
private List<String > items = new ArrayList <>();
public Order(String id) { this.id = id; }
public void addItem(String item) { items.add(item); }
// Getters
 }
 
Listing 6: Event Sourcing Example in Spring Boot (Continued)

This code implements an event store to persist order events and reconstructs an order’s state
by replaying them.

Core Idea

Don't store current state — store every event that led to it.

Traditional (State-based):          Event Sourcing:

Orders Table:                       Event Store:
┌──────────────────────┐            ┌─────────────────────────────┐
│ id  │ status  │ amt  │            │ 1. OrderPlaced    ($120)     │
│ 123 │ SHIPPED │ $120 │            │ 2. PaymentConfirmed          │
└──────────────────────┘            │ 3. ItemReserved              │
  Only current state                │ 4. OrderShipped              │
                                    └─────────────────────────────┘
                                      Full history — replay anytime
									  
Building Blocks
1. Events — Immutable Facts

// Base event
public abstract class DomainEvent {
    private final String aggregateId;
    private final LocalDateTime occurredAt;
    private final int version;

    protected DomainEvent(String aggregateId, int version) {
        this.aggregateId = aggregateId;
        this.occurredAt = LocalDateTime.now();
        this.version = version;
    }
    // getters...
}

// Concrete events — past tense, immutable facts
public class OrderPlacedEvent extends DomainEvent {
    private final String customerId;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;

    public OrderPlacedEvent(String orderId, String customerId,
                            List<OrderItem> items,
                            BigDecimal totalAmount, int version) {
        super(orderId, version);
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
}

public class OrderShippedEvent extends DomainEvent {
    private final String trackingNumber;
    private final LocalDateTime shippedAt;

    public OrderShippedEvent(String orderId,
                             String trackingNumber, int version) {
        super(orderId, version);
        this.trackingNumber = trackingNumber;
        this.shippedAt = LocalDateTime.now();
    }
}

public class OrderCancelledEvent extends DomainEvent {
    private final String reason;

    public OrderCancelledEvent(String orderId,
                               String reason, int version) {
        super(orderId, version);
        this.reason = reason;
    }
}

2. Aggregate — Rebuilt from Events

public class Order {

    private String orderId;
    private String customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItem> items;
    private String trackingNumber;
    private int version = 0;

    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    // ─── Factory — new order ───────────────────────────────────────
    public static Order place(String customerId,
                              List<OrderItem> items,
                              BigDecimal totalAmount) {
        Order order = new Order();
        String orderId = UUID.randomUUID().toString();

        // Raise event — don't set state directly
        order.applyAndRecord(new OrderPlacedEvent(
            orderId, customerId, items, totalAmount, 1
        ));
        return order;
    }

    // ─── Business operations ───────────────────────────────────────
    public void ship(String trackingNumber) {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only confirmed orders can be shipped"
            );
        }
        applyAndRecord(new OrderShippedEvent(
            orderId, trackingNumber, version + 1
        ));
    }

    public void cancel(String reason) {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                "Cannot cancel a shipped order"
            );
        }
        applyAndRecord(new OrderCancelledEvent(
            orderId, reason, version + 1
        ));
    }

    // ─── Apply events — mutate state ───────────────────────────────
    private void apply(OrderPlacedEvent e) {
        this.orderId = e.getAggregateId();
        this.customerId = e.getCustomerId();
        this.items = e.getItems();
        this.totalAmount = e.getTotalAmount();
        this.status = OrderStatus.PLACED;
    }

    private void apply(OrderShippedEvent e) {
        this.trackingNumber = e.getTrackingNumber();
        this.status = OrderStatus.SHIPPED;
    }

    private void apply(OrderCancelledEvent e) {
        this.status = OrderStatus.CANCELLED;
    }

    // ─── Dispatch ──────────────────────────────────────────────────
    private void applyAndRecord(DomainEvent event) {
        dispatchApply(event);                  // mutate state
        uncommittedEvents.add(event);          // stage for persistence
        this.version = event.getVersion();
    }

    private void dispatchApply(DomainEvent event) {
        if (event instanceof OrderPlacedEvent)
            apply((OrderPlacedEvent) event);
        else if (event instanceof OrderShippedEvent)
            apply((OrderShippedEvent) event);
        else if (event instanceof OrderCancelledEvent)
            apply((OrderCancelledEvent) event);
    }

    // ─── Rebuild from event history ────────────────────────────────
    public static Order reconstitute(List<DomainEvent> history) {
        Order order = new Order();
        for (DomainEvent event : history) {
            order.dispatchApply(event);        // replay — no recording
            order.version = event.getVersion();
        }
        return order;
    }

    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markEventsCommitted() {
        uncommittedEvents.clear();
    }
}

3. Event Store — Append-Only Log

// Stored record
public class EventRecord {
    private final String eventId;
    private final String aggregateId;
    private final String eventType;
    private final String payload;          // JSON serialized event
    private final int version;
    private final LocalDateTime occurredAt;

    public EventRecord(String aggregateId, DomainEvent event,
                       String payload) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = event.getClass().getSimpleName();
        this.payload = payload;
        this.version = event.getVersion();
        this.occurredAt = event.getOccurredAt();
    }
    // getters...
}

// Event store interface
public interface EventStore {
    void append(String aggregateId,
                List<DomainEvent> events,
                int expectedVersion);       // optimistic locking

    List<DomainEvent> load(String aggregateId);

    List<DomainEvent> loadFromVersion(String aggregateId,
                                      int fromVersion);  // for snapshots
}

// Implementation
@Repository
public class JdbcEventStore implements EventStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void append(String aggregateId,
                       List<DomainEvent> events,
                       int expectedVersion) {

        // Optimistic concurrency check
        Integer currentVersion = jdbc.queryForObject(
            "SELECT MAX(version) FROM event_store WHERE aggregate_id = ?",
            Integer.class, aggregateId
        );

        if (currentVersion != null && currentVersion != expectedVersion) {
            throw new OptimisticLockException(
                "Conflict on aggregate " + aggregateId
                + ". Expected v" + expectedVersion
                + " but found v" + currentVersion
            );
        }

        // Append events
        for (DomainEvent event : events) {
            String payload = serialize(event);
            jdbc.update(
                "INSERT INTO event_store " +
                "(event_id, aggregate_id, event_type, payload, version, occurred_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
                UUID.randomUUID().toString(),
                aggregateId,
                event.getClass().getSimpleName(),
                payload,
                event.getVersion(),
                event.getOccurredAt()
            );
        }
    }

    @Override
    public List<DomainEvent> load(String aggregateId) {
        return jdbc.query(
            "SELECT event_type, payload FROM event_store " +
            "WHERE aggregate_id = ? ORDER BY version ASC",
            (rs, rowNum) -> deserialize(
                rs.getString("event_type"),
                rs.getString("payload")
            ),
            aggregateId
        );
    }

    private String serialize(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    private DomainEvent deserialize(String type, String payload) {
        try {
            Class<?> clazz = Class.forName("com.example.events." + type);
            return (DomainEvent) objectMapper.readValue(payload, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed for: " + type, e);
        }
    }
}

4. Repository — Ties It Together

@Service
public class OrderRepository {

    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    public void save(Order order) {
        List<DomainEvent> newEvents = order.getUncommittedEvents();

        if (newEvents.isEmpty()) return;

        // Persist to event store
        eventStore.append(
            order.getOrderId(),
            newEvents,
            order.getVersion() - newEvents.size()  // expected version
        );

        // Publish for projections / CQRS read side
        newEvents.forEach(eventPublisher::publish);

        order.markEventsCommitted();
    }

    public Order findById(String orderId) {
        List<DomainEvent> history = eventStore.load(orderId);

        if (history.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        return Order.reconstitute(history);   // replay all events
    }
}

Event Store Schema:

CREATE TABLE event_store (
    event_id     VARCHAR(36)  PRIMARY KEY,
    aggregate_id VARCHAR(36)  NOT NULL,
    event_type   VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,       -- JSON
    version      INT          NOT NULL,
    occurred_at  TIMESTAMP    NOT NULL,

    UNIQUE (aggregate_id, version)            -- prevents duplicate versions
);

CREATE INDEX idx_aggregate_id ON event_store (aggregate_id, version);

Snapshots — Avoid Replaying 10,000 Events

public class OrderSnapshot {
    private final String orderId;
    private final OrderStatus status;
    private final BigDecimal totalAmount;
    private final int version;              // snapshot taken at this version
    private final LocalDateTime takenAt;
}

@Service
public class SnapshotOrderRepository {

    private static final int SNAPSHOT_THRESHOLD = 50;

    private final EventStore eventStore;
    private final SnapshotStore snapshotStore;

    public Order findById(String orderId) {
        OrderSnapshot snapshot = snapshotStore.findLatest(orderId);

        List<DomainEvent> events;

        if (snapshot != null) {
            // Only load events AFTER the snapshot
            events = eventStore.loadFromVersion(
                orderId, snapshot.getVersion()
            );
            Order order = Order.fromSnapshot(snapshot);
            events.forEach(order::applyFromHistory); // replay delta only
            return order;
        }

        // No snapshot — full replay
        events = eventStore.load(orderId);
        return Order.reconstitute(events);
    }

    public void save(Order order) {
        eventStore.append(order.getOrderId(),
                          order.getUncommittedEvents(),
                          order.getVersion());

        // Take snapshot every N events
        if (order.getVersion() % SNAPSHOT_THRESHOLD == 0) {
            snapshotStore.save(OrderSnapshot.from(order));
        }

        order.markEventsCommitted();
    }
}

Full Flow

Client: "Ship order 123"
       │
       ▼
  OrderCommandHandler
       │
       ├──► Load order 123 from Event Store
       │         │
       │    Replay events:
       │    [OrderPlaced] → [PaymentConfirmed] → [ItemReserved]
       │         │
       │    Order state: status=CONFIRMED ✓
       │
       ├──► order.ship("TRK-9876")
       │         │
       │    Validate: CONFIRMED → can ship ✓
       │    Raise: OrderShippedEvent (version=4)
       │
       ├──► EventStore.append(OrderShippedEvent)
       │         │
       │    event_store row inserted
       │
       └──► EventPublisher.publish(OrderShippedEvent)
                 │
            Kafka topic
                 │
       ┌─────────┴──────────┐
       ▼                    ▼
  CQRS Read Side        Notification
  (update view)         Service (email)

Event Sourcing + CQRS Together

┌─────────────────────────────────┐
                    │          Command Side            │
Write ─────────────►│  Handler → Aggregate → Events   │
                    │          Event Store             │
                    └──────────────┬──────────────────┘
                                   │ publish
                                   ▼
                              Event Bus
                            (Kafka / RabbitMQ)
                                   │
                    ┌──────────────┼──────────────┐
                    ▼              ▼               ▼
               Order View    Notification     Analytics
               (MongoDB)      Service         (Elastic)
                    │
Read ──────────────►│
                    └── fast, denormalized, no joins

When to Use Event Sourcing
✅ Audit trail required — finance, healthcare, compliance
✅ Time travel — replay state at any point in history
✅ Debugging — see exactly what happened and when
✅ Event-driven integrations — events are first-class citizens
✅ Already using CQRS — natural pairing					

==========================================================================

1.4 Saga Pattern

The Saga pattern manages distributed transactions across microservices using a sequence of
local transactions, with compensating transactions for rollbacks.

// OrderService.java
package com.example.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
@Autowired
private KafkaTemplate <String , String > kafkaTemplate;
@Autowired
private OrderRepository repository;

public void createOrder(Order order) {
repository.save(order);
kafkaTemplate.send("order -events", "OrderCreated:" +
order.getId());
 }


@KafkaListener(topics = "customer -events")
public void handleCustomerEvent(String event) {
if (event.startsWith("CreditReserved:")) {
String orderId = event.split(":")[1];
Order order = repository.findById(orderId).orElseThrow();
order.setStatus("Confirmed");
repository.save(order);
} else if (event.startsWith("CreditLimitExceeded:")) {
String orderId = event.split(":")[1];
repository.deleteById(orderId); // Compensating transaction
}
}
}

Listing 7: Choreography-based Saga in Spring Boot

// CustomerService.java
@Service
class CustomerService {
@Autowired
private KafkaTemplate <String , String > kafkaTemplate;

@KafkaListener(topics = "order -events")
public void handleOrderEvent(String event) {
if (event.startsWith("OrderCreated:")) {
 String orderId = event.split(":")[1];
 boolean creditAvailable = checkCredit();
 if (creditAvailable) {
 reserveCredit();
 kafkaTemplate.send("customer -events", "CreditReserved:" +
orderId);
 } else {
 kafkaTemplate.send("customer -events","CreditLimitExceeded:" + orderId);
}
}
}

private boolean checkCredit() { /* Logic */ return true; }
private void reserveCredit() { /* Logic */ }
}

Listing 8: Choreography-based Saga in Spring Boot (Continued)

This code shows a choreography-based saga where order and customer services communicate
via Kafka events, with compensating transactions for failures.


Core Idea

No distributed transactions — break it into local transactions with a rollback plan.

Traditional (2PC Distributed Transaction):    Saga Pattern:

BEGIN TRANSACTION                             Order Service    ✓ local tx
  orderService.create()      ← locks         Payment Service  ✓ local tx
  paymentService.charge()    ← locks         Inventory Service✓ local tx
  inventoryService.reserve() ← locks              │
COMMIT  ← all or nothing                          │ any failure?
         (tight coupling, deadlocks)              ▼
                                            Compensating transactions
                                            (undo what succeeded)
											
Two Saga Styles:

Choreography:                         Orchestration:

Order ──event──► Payment               Saga
  Payment ──event──► Inventory        Orchestrator
    Inventory ──event──► Shipping      │
                                       ├──► Order Service
  Services talk to each other          ├──► Payment Service
  No central coordinator               ├──► Inventory Service
  Hard to track flow                   └──► Shipping Service

                                       Central brain controls flow
                                       Easy to track, single SPOF											
1. Choreography-Based Saga

// ─── Step 1: Order Service ─────────────────────────────────────────
@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final EventPublisher publisher;

    public void placeOrder(PlaceOrderCommand cmd) {
        Order order = Order.create(
            cmd.getCustomerId(),
            cmd.getItems(),
            cmd.getTotalAmount()
        );
        order.markPending();
        orderRepo.save(order);

        // Kick off saga — publish event
        publisher.publish(new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getTotalAmount()
        ));
    }

    // Compensating transaction — undo order creation
    @EventListener
    public void on(PaymentFailedEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        order.cancel("Payment failed");          // local rollback
        orderRepo.save(order);

        publisher.publish(new OrderCancelledEvent(order.getId()));
    }

    @EventListener
    public void on(PaymentConfirmedEvent event) {
        Order order = orderRepo.findById(event.getOrderId());
        order.confirm();
        orderRepo.save(order);
    }
}

// ─── Step 2: Payment Service ───────────────────────────────────────
@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final EventPublisher publisher;

    @EventListener
    public void on(OrderCreatedEvent event) {
        try {
            Payment payment = Payment.charge(
                event.getCustomerId(),
                event.getTotalAmount()
            );
            paymentRepo.save(payment);

            publisher.publish(new PaymentConfirmedEvent(
                event.getOrderId(),
                payment.getId()
            ));
        } catch (InsufficientFundsException e) {
            publisher.publish(new PaymentFailedEvent(
                event.getOrderId(),
                "Insufficient funds"
            ));
        }
    }

    // Compensating transaction — refund
    @EventListener
    public void on(InventoryReservationFailedEvent event) {
        Payment payment = paymentRepo
            .findByOrderId(event.getOrderId());
        payment.refund();                        // local rollback
        paymentRepo.save(payment);

        publisher.publish(new PaymentRefundedEvent(event.getOrderId()));
    }
}

// ─── Step 3: Inventory Service ─────────────────────────────────────
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final EventPublisher publisher;

    @EventListener
    public void on(PaymentConfirmedEvent event) {
        try {
            inventoryRepo.reserve(event.getOrderId());

            publisher.publish(new InventoryReservedEvent(
                event.getOrderId()
            ));
        } catch (OutOfStockException e) {
            publisher.publish(new InventoryReservationFailedEvent(
                event.getOrderId(),
                "Out of stock"
            ));
        }
    }

    // Compensating transaction — release reservation
    @EventListener
    public void on(OrderCancelledEvent event) {
        inventoryRepo.release(event.getOrderId()); // local rollback
    }
}

Choreography Flow:

OrderService          PaymentService        InventoryService
     │                      │                      │
     │── OrderCreated ──────►│                      │
     │                       │── PaymentConfirmed ──►│
     │◄── PaymentConfirmed ──│                      │
     │                                              │── InventoryReserved
     │                                              │
     │           ✓ Happy Path Complete              │


     │           ✗ Failure — Inventory Out of Stock
     │                                              │
     │                                   InventoryReservationFailed
     │                      │◄────────────────────────────────────
     │                 Refund payment
     │◄── PaymentRefunded ──│
     │  Cancel order        │
     │── OrderCancelled ────────────────────────────►│
                                              Release reservation
											  
											  
2. Orchestration-Based Saga:

// ─── Saga State ────────────────────────────────────────────────────
public enum SagaStep {
    STARTED,
    ORDER_CREATED,
    PAYMENT_CONFIRMED,
    INVENTORY_RESERVED,
    SHIPPING_SCHEDULED,
    COMPLETED,
    // Compensation steps
    CANCELLING_INVENTORY,
    REFUNDING_PAYMENT,
    CANCELLING_ORDER,
    FAILED
}

public class PlaceOrderSagaState {
    private final String sagaId;
    private final String orderId;
    private final String customerId;
    private final BigDecimal totalAmount;
    private SagaStep currentStep;
    private String failureReason;

    // getters + setters...
}

// ─── Orchestrator ──────────────────────────────────────────────────
@Service
public class PlaceOrderSagaOrchestrator {

    private final SagaStateRepository sagaRepo;
    private final OrderServiceClient orderClient;
    private final PaymentServiceClient paymentClient;
    private final InventoryServiceClient inventoryClient;
    private final ShippingServiceClient shippingClient;

    // ── Start saga ─────────────────────────────────────────────────
    public void start(PlaceOrderCommand cmd) {
        PlaceOrderSagaState saga = new PlaceOrderSagaState(
            UUID.randomUUID().toString(),
            cmd.getCustomerId(),
            cmd.getTotalAmount()
        );
        saga.setCurrentStep(SagaStep.STARTED);
        sagaRepo.save(saga);

        createOrder(saga, cmd);
    }

    // ── Step 1: Create Order ───────────────────────────────────────
    private void createOrder(PlaceOrderSagaState saga,
                             PlaceOrderCommand cmd) {
        try {
            String orderId = orderClient.createOrder(
                cmd.getCustomerId(),
                cmd.getItems()
            );
            saga.setOrderId(orderId);
            saga.setCurrentStep(SagaStep.ORDER_CREATED);
            sagaRepo.save(saga);

            chargePayment(saga);               // next step
        } catch (Exception e) {
            fail(saga, "Order creation failed: " + e.getMessage());
        }
    }

    // ── Step 2: Charge Payment ─────────────────────────────────────
    private void chargePayment(PlaceOrderSagaState saga) {
        try {
            paymentClient.charge(
                saga.getCustomerId(),
                saga.getOrderId(),
                saga.getTotalAmount()
            );
            saga.setCurrentStep(SagaStep.PAYMENT_CONFIRMED);
            sagaRepo.save(saga);

            reserveInventory(saga);            // next step
        } catch (Exception e) {
            saga.setFailureReason(e.getMessage());
            compensateCancelOrder(saga);       // rollback
        }
    }

    // ── Step 3: Reserve Inventory ──────────────────────────────────
    private void reserveInventory(PlaceOrderSagaState saga) {
        try {
            inventoryClient.reserve(saga.getOrderId());
            saga.setCurrentStep(SagaStep.INVENTORY_RESERVED);
            sagaRepo.save(saga);

            scheduleShipping(saga);            // next step
        } catch (Exception e) {
            saga.setFailureReason(e.getMessage());
            compensateRefundPayment(saga);     // rollback
        }
    }

    // ── Step 4: Schedule Shipping ──────────────────────────────────
    private void scheduleShipping(PlaceOrderSagaState saga) {
        try {
            shippingClient.schedule(saga.getOrderId());
            saga.setCurrentStep(SagaStep.COMPLETED);
            sagaRepo.save(saga);

            System.out.println("Saga completed: " + saga.getSagaId());
        } catch (Exception e) {
            saga.setFailureReason(e.getMessage());
            compensateCancelInventory(saga);   // rollback
        }
    }

    // ── Compensations (reverse order) ─────────────────────────────
    private void compensateCancelInventory(PlaceOrderSagaState saga) {
        saga.setCurrentStep(SagaStep.CANCELLING_INVENTORY);
        sagaRepo.save(saga);

        try {
            inventoryClient.release(saga.getOrderId());
        } catch (Exception e) {
            // log + retry — compensation must eventually succeed
        }
        compensateRefundPayment(saga);
    }

    private void compensateRefundPayment(PlaceOrderSagaState saga) {
        saga.setCurrentStep(SagaStep.REFUNDING_PAYMENT);
        sagaRepo.save(saga);

        try {
            paymentClient.refund(saga.getOrderId());
        } catch (Exception e) {
            // log + retry
        }
        compensateCancelOrder(saga);
    }

    private void compensateCancelOrder(PlaceOrderSagaState saga) {
        saga.setCurrentStep(SagaStep.CANCELLING_ORDER);
        sagaRepo.save(saga);

        try {
            orderClient.cancel(saga.getOrderId(), saga.getFailureReason());
        } catch (Exception e) {
            // log + retry
        }

        fail(saga, saga.getFailureReason());
    }

    private void fail(PlaceOrderSagaState saga, String reason) {
        saga.setCurrentStep(SagaStep.FAILED);
        saga.setFailureReason(reason);
        sagaRepo.save(saga);

        System.err.println("Saga failed [" + saga.getSagaId()
            + "]: " + reason);
    }
}

Orchestration Flow:

PlaceOrderSagaOrchestrator
                    │
       ┌────────────┼─────────────┐
       ▼            ▼             ▼
  ① Create      ② Charge     ③ Reserve
    Order        Payment      Inventory
       │            │             │
       ✓            ✓             ✗ OutOfStock
                                  │
                         ┌────────┘
                         ▼
                  Compensate (reverse):
                  ③ —           (nothing reserved)
                  ② Refund      payment
                  ① Cancel      order
                         │
                         ▼
                      FAILED

Idempotency — Critical for Sagas:

// Every step must be safe to retry
@Service
public class PaymentService {

    public void charge(String orderId, BigDecimal amount) {
        // Check if already processed — idempotency key
        if (paymentRepo.existsByOrderId(orderId)) {
            return;   // already charged — safe to return
        }

        Payment payment = Payment.charge(orderId, amount);
        paymentRepo.save(payment);
    }

    public void refund(String orderId) {
        Payment payment = paymentRepo.findByOrderId(orderId);

        if (payment == null) return;            // nothing to refund
        if (payment.isRefunded()) return;       // already refunded

        payment.refund();
        paymentRepo.save(payment);
    }
}

Choreography vs Orchestration

                       Choreography                     Orchestration		
Coupling                 Loose                     Services depend on orchestrator
Visibility               Hard to trace flow        Easy — one place to check
Complexity               Grows with steps          Contained in orchestrator
Single point of failure  No                        Yes (mitigate with HA)
Best for                 Simple, few steps         Complex, many steps

Saga State Table:

CREATE TABLE saga_state (
    saga_id        VARCHAR(36) PRIMARY KEY,
    order_id       VARCHAR(36),
    customer_id    VARCHAR(36)  NOT NULL,
    total_amount   DECIMAL      NOT NULL,
    current_step   VARCHAR(50)  NOT NULL,
    failure_reason TEXT,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

saga_id   │ order_id │ current_step        │ failure_reason
──────────┼──────────┼─────────────────────┼────────────────────
abc-001   │ ord-101  │ COMPLETED           │ null
abc-002   │ ord-102  │ FAILED              │ Insufficient funds
abc-003   │ ord-103  │ REFUNDING_PAYMENT   │ Out of stock
abc-004   │ ord-104  │ PAYMENT_CONFIRMED   │ null  ← in progress


When to Use Saga
✅ Distributed transactions across microservices
✅ Long-running business processes (order → payment → shipping)
✅ Services owned by different teams
✅ Need audit trail of each step
✅ Eventual consistency is acceptable			  
========================================================================

1.5 Backend for Frontend (BFF)

The BFF pattern introduces a layer to aggregate and transform data from multiple microservices, simplifying frontend logic.

Traditional:                    BFF Pattern:
                                
Web App ──┐                     Web App ──── Web BFF ──┐
          ├──► General API      Mobile  ── Mobile BFF ──┼──► Downstream Services
Mobile  ──┘                     TV App  ──── TV BFF  ──┘

The Backend for Frontend (BFF) pattern creates specialized backend services for each frontend client (e.g., mobile, desktop, web), providing tailored APIs that reduce over-fetching and improve performance. It acts as a customized middleware layer between frontend applications and downstream microservices, optimizing data formats and handling client-specific security or authentication requirements

// BffController.java
package com.example.bff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/bff")
public class BffController {
@Autowired
private RestTemplate restTemplate;

@GetMapping("/user -orders/{userId}")
public UserOrderView getUserOrders(@PathVariable String userId) {
// Fetch user data
User user =
restTemplate.getForObject("http://user -service/users/{id}",
User.class , userId);
// Fetch order data
Order[] orders =
restTemplate.getForObject("http://order -service/orders?userId={id}",
Order[].class , userId);
// Aggregate and transform
return new UserOrderView(user.getName(), Arrays.asList(orders));
}
}

// UserOrderView.java
class UserOrderView {
private String userName;
private List<Order > orders;
public UserOrderView(String userName , List<Order > orders) {
this.userName = userName;
this.orders = orders;
}
// Getters
}

Listing 9: BFF Example in Spring Boot

This code demonstrates a BFF aggregating user and order data into a single response for the
frontend.

===================================================================

1.6 API Gateway

The API Gateway acts as a single entry point, routing client requests to microservices and
handling cross-cutting concerns like authentication.

The API Gateway pattern is an architectural design where a single entry point acts as a "front door" for all client requests in a microservices system. Instead of clients calling dozens of individual microservices directly, they interact only with the gateway, which then routes, aggregates, or processes those requests

Core FunctionalitiesRequest Routing: Automatically directs incoming traffic to the correct backend service based on URL paths or headers.

API Composition (Aggregation): Combines data from multiple services into a single response, reducing "chattiness" and the number of round-trips for mobile or web clients.

Cross-Cutting Concerns (Offloading): Centralizes shared tasks like authentication, SSL termination, rate limiting, and logging so individual microservices don't have to implement them.

Protocol Translation: Converts between different protocols, such as allowing a web client to use HTTP while the internal service uses gRPC or AMQP

// GatewayApplication.java
package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
public static void main(String[] args) {
SpringApplication.run(GatewayApplication.class , args);
}

@Bean

public RouteLocator routes(RouteLocatorBuilder builder) {
return builder.routes()
.route("user", r -> r.path("/users/**")
.filters(f -> f.addRequestHeader("Authorization","Bearer token"))
.uri("http://user -service"))
.route("order", r -> r.path("/orders/**")
.filters(f -> f.addRequestHeader("Authorization","Bearer token"))
.uri("http://order -service"))
.build();
 }
 }
 
Listing 10: API Gateway in Spring Cloud Gateway

This code configures a Spring Cloud Gateway to route requests to user and order services with
authentication headers.

Common VariationsBackends for Frontends (BFF): A specialized version where separate gateways are created for different client types (e.g., one for iOS, one for Android, and one for Web) to provide optimized data formats for each.

Service Mesh Integration: In complex systems, an API Gateway manages "North-South" traffic (external to internal), while a service mesh handles "East-West" traffic (internal service-to-service

Reverse proxy or gateway routing:

This is part of gateway routing pattern features. The API Gateway provides reverse proxy to redirect requests to the endpoints of the internal microservices. Usually, It is using layer 7 routing for HTTP requests for request redirections. This routing feature provides to decouple client applications from the internal microservices. So it is separating responsibilities on network layer. Another benefit is abstracting internal operations, API GW provide abstraction over the backend microservices, so even there is changes on backend microservices, it wont be affect to client applications. That means don’t need to update client applications when changing backend services.

Requests aggregation:

This is part of gateway aggregation pattern features. API Gateway can aggregate multiple internal microservices into a single client request. With this approach, the client application sends a single request to the API Gateway. After that API Gateway dispatches several requests to the internal microservices and then aggregates the results and sends everything back to the client application in 1 single response. The main benefit of this gateway aggregation pattern is to reduce chattiness communication between the client applications and the backend microservices.

Cross-cutting concerns and gateway offloading:

This is part of gateway offloading pattern features. Since API Gateway handle client request in centralized placed, its best practice to implement cross cutting functionality on the API Gateways.

========================================================================

1.7 Strangler Pattern

The Strangler pattern gradually migrates a monolithic application to microservices by incrementally replacing functionality.

// StranglerProxyController.java
package com.example.strangler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class StranglerProxyController {
@Autowired
private RestTemplate restTemplate;

@GetMapping("/products/{id}")
public Product getProduct(@PathVariable String id) {
// Gradually migrate: Route to new microservice or legacy monolith
if (shouldRouteToMicroservice(id)) {
return
restTemplate.getForObject("http://product -service/products/{id}",
Product.class , id);
} else {
return
restTemplate.getForObject("http://monolith/legacy/products/{id}",
Product.class , id);
}
}

private boolean shouldRouteToMicroservice(String id) {
// Logic to determine if microservice handles this product
return id.startsWith("new-");
}
}

Listing 11: Strangler Pattern Proxy in Spring Boot


This code shows a proxy routing product requests to either a new microservice or the legacy
monolith based on criteria.

=======================================================================

1.8 Circuit Breaker

The Circuit Breaker pattern prevents cascading failures by stopping requests to a failing service
and allowing recovery time.

The circuit breaker pattern in microservices is a resilience design pattern that prevents a cascading failure by halting requests to a failing service. It acts as a proxy monitoring for failures, tripping to an "Open" state to fail fast after a threshold is reached, protecting resources and allowing for graceful degradation

The circuit breaker operates as a state machine:

Closed: Requests pass normally. If failure rate threshold is exceeded, it trips to Open.

Open: Requests fail immediately, returning an error without calling the downstream service.

Half-Open: After a timeout, allows a limited number of test requests. If these succeed, it returns to Closed. If they fail, it returns to Open

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>


2. Configure application.yml

Define the threshold and timing for your circuit breaker:

resilience4j:
  circuitbreaker:
    instances:
      userServiceBreaker:
        registerHealthIndicator: true
        slidingWindowSize: 10
        permittedNumberOfCallsInHalfOpenState: 3
        slidingWindowType: COUNT_BASED
        minimumNumberOfCalls: 5
        waitDurationInOpenState: 5s
        failureRateThreshold: 50


3. Apply Annotation in ServiceUse @CircuitBreaker on methods that call external services. Specify a fallbackMethod to handle failures gracefully

@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "userServiceFallback")
    public String getUserDetails(String userId) {
        // External call to User Microservice
        return restTemplate.getForObject("http://user-service/users/" + userId, String.class);
    }

    // Fallback method must have the same signature + Throwable parameter
    public String userServiceFallback(String userId, Throwable t) {
        return "User Service is currently unavailable. Please try again later.";
    }
}

another example:


// OrderClient.java
package com.example.order;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderClient {
@Autowired
private RestTemplate restTemplate;

@CircuitBreaker(name = "orderService", fallbackMethod = "fallback")
public Order getOrder(String orderId) {
return
restTemplate.getForObject("http://order -service/orders/{id}",Order.class , orderId);
}

public Order fallback(String orderId , Throwable t) {
// Return default or cached response
return new Order(orderId , "Unavailable", 0.0);
}
}

// application.yml
resilience4j.circuitbreaker:
instances:
orderService:
slidingWindowSize: 10
failureRateThreshold: 50
waitDurationInOpenState: 30000

How Each Property Works
slidingWindowSize: 10
The circuit breaker evaluates the last N calls to decide health.

Call history (sliding window):
[✓][✓][✗][✗][✓][✗][✗][✓][✗][✗]
 1   2   3   4   5   6   7   8   9  10
                                      ↑
                              window evaluated here
5 failures out of 10 = 50% failure rate

Default window type is COUNT_BASED (counts calls). Can also be TIME_BASED (counts calls in last N seconds).

failureRateThreshold: 50
If failure rate ≥ 50%, the circuit opens (blocks calls).

Failure rate < 50%  →  CLOSED  (normal operation)
Failure rate ≥ 50%  →  OPEN    (calls blocked immediately)

waitDurationInOpenState: 30000
After opening, the circuit waits 30,000 ms (30 seconds) before moving to HALF-OPEN to test recovery.

OPEN ──(wait 30s)──► HALF-OPEN ──(probe calls)──► CLOSED or OPEN

Full State Machine

failure rate ≥ 50%
CLOSED ──────────────────────► OPEN
  ▲                               │
  │                          wait 30s
  │                               │
  │       probe calls succeed     ▼
  └──────────────────────── HALF-OPEN
                                  │
                          probe calls fail
                                  │
                                  ▼
                                OPEN (reset timer)

Listing 12: Circuit Breaker in Spring Boot with Resilience4j

This code uses Resilience4j to implement a circuit breaker for order service calls with a fallback method.

Adding RateLimiter to Resilience4j Config

resilience4j:
  circuitbreaker:
    instances:
      orderService:
        slidingWindowSize: 10
        minimumNumberOfCalls: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30000

  ratelimiter:
    instances:
      orderService:
        limitForPeriod: 100
        limitRefreshPeriod: 1s
        timeoutDuration: 500ms
		
Property Breakdown
limitForPeriod: 100
Max 100 requests allowed per refresh cycle.

[========== 100 permits ==========]
 Each request consumes 1 permit
 101st request → blocked or rejected

limitRefreshPeriod: 1s
Permits reset every 1 second (not rolling — hard reset).

t=0s  →  100 permits available
t=0.3s → 40 permits left (60 used)
t=1s  →  100 permits restored  ← hard reset
t=1.3s → 40 permits left again

timeoutDuration: 500ms
How long a request waits for a permit before throwing RequestNotPermitted.

Permit available immediately  →  proceeds
No permit, waits up to 500ms  →  proceeds if one frees up
Still no permit after 500ms   →  exception thrown

How They Work Together:

Incoming Request
       │
       ▼
 ┌─────────────┐   No permit /
 │ Rate Limiter │──timeout exceeded──► 429 Too Many Requests
 └──────┬──────┘
        │ Permit granted
        ▼
 ┌─────────────┐   Circuit OPEN
 │Circuit Breaker│──────────────────► 503 Service Unavailable
 └──────┬──────┘
        │ Circuit CLOSED
        ▼
   orderService

Rate Limiter should wrap outside the Circuit Breaker — throttle first, then protect.

Spring Boot — Applying Both

@Service
public class OrderService {

    @RateLimiter(name = "orderService", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "orderService", fallbackMethod = "circuitFallback")
    public Order getOrder(String orderId) {
        return orderClient.fetchOrder(orderId);
    }

    public Order rateLimitFallback(String orderId, RequestNotPermittedException ex) {
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }

    public Order circuitFallback(String orderId, CallNotPermittedException ex) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");
    }
}

Annotation Order Matters

// ✅ Correct — Rate limiter is outermost
@RateLimiter(name = "orderService")
@CircuitBreaker(name = "orderService")
public Order getOrder() { ... }

// ❌ Wrong — Circuit breaker would see rate limit exceptions as failures
@CircuitBreaker(name = "orderService")
@RateLimiter(name = "orderService")
public Order getOrder() { ... }

Tuning Reference
PropertyYour ConfigTypical ValuesNoteslimitForPeriod10050–1000Match your SLA / downstream capacitylimitRefreshPeriod1s1s / 500msShorter = smoother throttlingtimeoutDuration500ms0–1000ms0ms = fail fast, no waiting

Set timeoutDuration: 0 for strict fail-fast behavior — no queuing, immediate rejection when limit is hit.

Rate Limiter — 10 Requests per 60 Seconds (Java 8)
Config

resilience4j:
  ratelimiter:
    instances:
      orderService:
        limitForPeriod: 10
        limitRefreshPeriod: 60s
        timeoutDuration: 0ms      # fail fast, no waiting

Implementation

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

import java.time.Duration;
import java.util.function.Supplier;

public class OrderServiceClient {

    private final RateLimiter rateLimiter;
    private final OrderClient orderClient;

    public OrderServiceClient(OrderClient orderClient) {
        this.orderClient = orderClient;

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)                          // 10 requests
                .limitRefreshPeriod(Duration.ofSeconds(60)) // per 60 seconds
                .timeoutDuration(Duration.ofMillis(0))       // fail fast
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        this.rateLimiter = registry.rateLimiter("orderService");
    }

    // Decorated supplier — Java 8 style
    public Order getOrder(String orderId) {
        Supplier<Order> supplier = RateLimiter
                .decorateSupplier(rateLimiter, () -> orderClient.fetchOrder(orderId));

        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback(orderId, e);
        }
    }

    private Order fallback(String orderId, Exception e) {
        System.err.println("Rate limit exceeded for order: " + orderId);
        throw new RuntimeException("Too many requests. Try again later.", e);
    }
}

With Checked Exception Handling

public Order getOrder(String orderId) {
    // Check permit before calling — explicit Java 8 style
    boolean permitted = rateLimiter.acquirePermission();

    if (!permitted) {
        throw new RuntimeException("Rate limit exceeded — max 10 requests per 60s");
    }

    return orderClient.fetchOrder(orderId);
}

Monitoring Metrics (Java 8)

public void printMetrics() {
    RateLimiter.Metrics metrics = rateLimiter.getMetrics();

    System.out.println("Available permits : " + metrics.getAvailablePermissions());
    System.out.println("Waiting threads   : " + metrics.getNumberOfWaitingThreads());
}

Available permits : 7      ← 3 used out of 10
Waiting threads   : 0      ← nobody queued (timeoutDuration=0ms)

How It Behaves

t=0s   → Request 1–10  ✓  (permits consumed)
t=0s   → Request 11    ✗  RequestNotPermitted (fail fast, no wait)

t=60s  → Permits reset to 10
t=60s  → Request 12–21 ✓  (fresh window)
		
Full Working Example

public class Main {

    public static void main(String[] args) {

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(60))
                .timeoutDuration(Duration.ofMillis(0))
                .build();

        RateLimiter rateLimiter = RateLimiterRegistry
                .of(config)
                .rateLimiter("orderService");

        for (int i = 1; i <= 15; i++) {
            final int requestNum = i;
            Supplier<String> supplier = RateLimiter.decorateSupplier(
                    rateLimiter,
                    () -> "Response for request #" + requestNum
            );

            try {
                String result = supplier.get();
                System.out.println("✓ Request " + requestNum + " → " + result);
            } catch (Exception e) {
                System.out.println("✗ Request " + requestNum + " → BLOCKED: " + e.getMessage());
            }
        }
    }
}
O/P
✓ Request 1  → Response for request #1
✓ Request 2  → Response for request #2
✓ Request 3  → Response for request #3
✓ Request 4  → Response for request #4
✓ Request 5  → Response for request #5
✓ Request 6  → Response for request #6
✓ Request 7  → Response for request #7
✓ Request 8  → Response for request #8
✓ Request 9  → Response for request #9
✓ Request 10 → Response for request #10
✗ Request 11 → BLOCKED: RateLimiter 'orderService' does not permit further calls
✗ Request 12 → BLOCKED: RateLimiter 'orderService' does not permit further calls
✗ Request 13 → BLOCKED: RateLimiter 'orderService' does not permit further calls
✗ Request 14 → BLOCKED: RateLimiter 'orderService' does not permit further calls
✗ Request 15 → BLOCKED: RateLimiter 'orderService' does not permit further calls

Dependency:

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
    <version>1.7.1</version>   <!-- last stable for Java 8 -->
</dependency>

Use 1.7.x — Resilience4j 2.x requires Java 17+.
=========================================================================

1.9 Externalized Configuration

Externalized configuration stores microservice configurations in an external source, enabling
runtime updates without redeployment.

// ProductServiceApplication.java
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ProductServiceApplication {
public static void main(String[] args) {
SpringApplication.run(ProductServiceApplication.class , args);
}

@Bean
@ConfigurationProperties(prefix = "app.config")
public AppConfig appConfig() {
return new AppConfig();
}
}

// AppConfig.java
public class AppConfig {
private String databaseUrl;
private int maxConnections;
// Getters and setters
}

Listing 13: Externalized Configuration in Spring Boot

// application.yml (loaded from external config server)
spring:
cloud:
config:
uri: http://config -server:8888
# External config: http://config -server/product -service.yml
# app:
# config:
# databaseUrl: mongodb://localhost :27017
# maxConnections: 100

Listing 14: Externalized Configuration in Spring Boot (Continued)

This code uses Spring Cloud Config to load configuration from an external server.

=============================================================================

1.10 Consumer-Driven Contract Testing

Consumer-Driven Contract Testing ensures service providers meet consumer expectations by
defining and testing contracts.

// PactConsumerTest.java
package com.example.order;

import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8080")
public class PactConsumerTest {
@Test
void testUserServiceContract() {
RestTemplate restTemplate = new RestTemplate();

User user =
restTemplate.getForObject("http://localhost :8080/users/1",User.class);
assert user.getId().equals("1");
assert user.getName() != null;
}
}

Listing 15: Contract Testing with Pact in Spring Boot

// UserServicePact.java
@Pact(provider = "UserService", consumer = "OrderService")
public RequestResponsePact createPact(PactDslWithProvider builder) {
return builder
.given("user exists")
.uponReceiving("a request for user")
.path("/users/1")
.method("GET")
.willRespondWith()
.status(200)
.body("{\"id\": \"1\", \"name\": \"John Doe\"}")
.toPact();
 }
 
Listing 16: Contract Testing with Pact in Spring Boot (Continued)
This code uses Pact to define and test a contract between the order service (consumer) and user
service (provider)

