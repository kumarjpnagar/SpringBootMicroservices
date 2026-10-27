
SOLID Principles (Hands-on Explanation)

SOLID =

S → Single Responsibility Principle

O → Open/Closed Principle

L → Liskov Substitution Principle

I → Interface Segregation Principle

D → Dependency Inversion Principle

==============================================

1️⃣ Single Responsibility Principle (SRP)
Definition

A class should have only one reason to change.

❌ Bad Design

class UserService {

    public void login(String username, String password) {
        System.out.println("User logged in");
    }

    public void updateProfile() {
        System.out.println("Profile updated");
    }

    public void sendEmail() {
        System.out.println("Email sent");
    }
}

Problem:

One class doing 3 responsibilities

Authentication

Profile management

Email sending

This violates SRP.

✅ Good Design

class UserAuth {
    public void login(String username, String password) {
        System.out.println("User logged in");
    }
}

class UserProfile {
    public void updateProfile() {
        System.out.println("Profile updated");
    }
}

class EmailService {
    public void sendEmail() {
        System.out.println("Email sent");
    }
}

Now

Class	Responsibility
UserAuth	Authentication
UserProfile	Profile
EmailService	Email

Benefits

Easier maintenance

Easier testing

Cleaner code

2️⃣ Open Closed Principle (OCP)
Definition

Software should be:

✔ Open for extension
❌ Closed for modification

Meaning:
we should add new functionality without changing existing code.

❌ Bad Design

class AreaCalculator {

    public double calculateArea(Object shape) {

        if(shape instanceof Circle){
            Circle c = (Circle) shape;
            return Math.PI * c.radius * c.radius;
        }

        if(shape instanceof Rectangle){
            Rectangle r = (Rectangle) shape;
            return r.length * r.width;
        }

        return 0;
    }
}

Problem:
Every time a new shape comes → modify code.

✅ Good Design

interface Shape {
    double area();
}

Circle

class Circle implements Shape {

    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double area() {
        return Math.PI * radius * radius;
    }
}

Rectangle

class Rectangle implements Shape {

    double length;
    double width;

    public Rectangle(double l, double w) {
        length = l;
        width = w;
    }

    public double area() {
        return length * width;
    }
}

Calculator

class AreaCalculator {

    public double calculateArea(Shape shape) {
        return shape.area();
    }
}

Now adding Triangle requires no modification.

3️⃣ Liskov Substitution Principle (LSP)
Definition

A subclass must be replaceable with its parent class without breaking behavior.

Example

class Bird {
    void fly() {
        System.out.println("Bird can fly");
    }
}

Subclasses

class Eagle extends Bird {
    void fly() {
        System.out.println("Eagle flying");
    }
}

class Sparrow extends Bird {
    void fly() {
        System.out.println("Sparrow flying");
    }
}

Usage

public void makeFly(Bird bird) {
    bird.fly();
}

Now

makeFly(new Eagle());
makeFly(new Sparrow());

Works perfectly.

❌ LSP Violation Example

class Penguin extends Bird {

    void fly() {
        throw new UnsupportedOperationException();
    }
}

Penguins cannot fly, so this breaks LSP.

Better design:

class Bird { }

interface Flyable {
    void fly();
}

4️⃣ Interface Segregation Principle (ISP)
Definition

Clients should not depend on interfaces they do not use.

❌ Bad Design

interface Machine {

    void print();

    void scan();

    void fax();
}

Problem:

A simple printer cannot fax or scan.

class SimplePrinter implements Machine {

    public void print() { }

    public void scan() {
        throw new UnsupportedOperationException();
    }

    public void fax() {
        throw new UnsupportedOperationException();
    }
}

Bad design.

✅ Good Design

Split interfaces.

interface Printable {
    void print();
}

interface Scannable {
    void scan();
}

interface Faxable {
    void fax();
}

Printer

class SimplePrinter implements Printable {

    public void print() {
        System.out.println("Printing");
    }
}

MultiFunction Printer

class MultiFunctionPrinter implements Printable, Scannable, Faxable {

    public void print(){}

    public void scan(){}

    public void fax(){}
}

Now classes only implement what they need.

5️⃣ Dependency Inversion Principle (DIP)
Definition

High level modules should not depend on low level modules.

Both should depend on abstraction (interfaces).

❌ Bad Design

class StripePayment {

    public void pay(double amount) {
        System.out.println("Paid using Stripe");
    }
}

class OrderService {

    StripePayment stripe = new StripePayment();

    public void placeOrder(double amount) {
        stripe.pay(amount);
    }
}

Problem

OrderService tightly coupled with Stripe.

✅ Good Design

Create abstraction.

interface PaymentGateway {

    void pay(double amount);
}

Stripe

class StripePayment implements PaymentGateway {

    public void pay(double amount) {
        System.out.println("Paid via Stripe");
    }
}

PayPal

class PayPalPayment implements PaymentGateway {

    public void pay(double amount) {
        System.out.println("Paid via PayPal");
    }
}

OrderService

class OrderService {

    PaymentGateway gateway;

    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void placeOrder(double amount) {
        gateway.pay(amount);
    }
}

Usage

PaymentGateway payment = new StripePayment();
OrderService order = new OrderService(payment);

order.placeOrder(1000);
In Spring Boot (Real Example)

Dependency Injection

@Service
class OrderService {

    @Autowired
    PaymentGateway gateway;
}

Spring injects the implementation.

Why SOLID Principles Matter

They make systems:

✔ Easier to maintain
✔ Easier to test
✔ Easier to extend
✔ Loosely coupled
✔ Highly scalable

Used heavily in:

Spring Boot

Microservices

Clean Architecture

DDD

Real Microservice Example (Important for Interviews)

Example in Payment Microservice

OrderService
     |
PaymentGateway (interface)
     |
  ---------------------
  |                   |
StripeService     PayPalService

If tomorrow RazorPay added:

Just add

RazorPayService implements PaymentGateway

No modification required.

✅ This is SOLID + OCP + DIP working together.

====================================================
Since we work with Spring Boot microservices (like Incentives services you mentioned), I’ll showa complete real-world example using Controller → Service → Repository architecture applying all SOLID principles. 🚀

We will design a small Order Payment Microservice.

Architecture:

Controller
    ↓
Service
    ↓
PaymentGateway (Interface)
    ↓
StripePayment | PayPalPayment
    ↓
Repository

1️⃣ Single Responsibility Principle (SRP)

Each class has one responsibility.

Controller → Handles HTTP requests
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/pay")
    public String pay(@RequestParam double amount) {
        orderService.processPayment(amount);
        return "Payment Successful";
    }
}

Responsibility:

Only API handling

Service → Business logic

@Service
public class OrderService {

    private final PaymentGateway paymentGateway;

    public OrderService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void processPayment(double amount) {
        paymentGateway.pay(amount);
    }
}

Responsibility:

Only business logic

Repository → Data access
@Repository
public class OrderRepository {

    public void saveOrder(double amount) {
        System.out.println("Saving order to DB");
    }
}

Responsibility:

Only database operations

2️⃣ Open Closed Principle (OCP)

The system should be open for extension but closed for modification.

Create abstraction.

public interface PaymentGateway {

    void pay(double amount);
}
Stripe implementation
@Component
public class StripePayment implements PaymentGateway {

    @Override
    public void pay(double amount) {
        System.out.println("Payment via Stripe: " + amount);
    }
}

PayPal implementation

@Component
public class PaypalPayment implements PaymentGateway {

    @Override
    public void pay(double amount) {
        System.out.println("Payment via PayPal: " + amount);
    }
}

Now if tomorrow you add RazorPay, you don't modify existing code.

@Component
public class RazorPayPayment implements PaymentGateway {

    @Override
    public void pay(double amount) {
        System.out.println("Payment via RazorPay");
    }
}

This follows OCP.

3️⃣ Liskov Substitution Principle (LSP)

Any implementation should be replaceable without breaking functionality.

Example:

PaymentGateway payment = new StripePayment();

or

PaymentGateway payment = new PaypalPayment();

Both work with:

payment.pay(500);

Service code does not change.

4️⃣ Interface Segregation Principle (ISP)

Instead of one large interface.

❌ Bad interface

interface PaymentSystem {

    void pay();
    void refund();
    void generateInvoice();
}

Some systems may not support refund or invoice.

✅ Better design

interface PaymentGateway {
    void pay(double amount);
}

interface Refundable {
    void refund(double amount);
}

interface InvoiceGenerator {
    void generateInvoice();
}

Now classes implement only required interfaces.

5️⃣ Dependency Inversion Principle (DIP)

High level module should depend on abstraction not implementation.

Service should depend on interface.

@Service
public class OrderService {

    private final PaymentGateway paymentGateway;

    public OrderService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void processPayment(double amount) {
        paymentGateway.pay(amount);
    }
}

Spring injects the implementation automatically.

Spring Dependency Injection
@Autowired
PaymentGateway paymentGateway;

Spring will inject:

StripePayment
or
PaypalPayment
Complete Flow Diagram (Real Microservice)
Client
   |
   v
OrderController
   |
   v
OrderService
   |
   v
PaymentGateway (Interface)
   |
   |----------------------|
   v                      v
StripePayment        PaypalPayment
   |
   v
OrderRepository
   |
   v
Database

Real Interview Question

Interviewer often asks:

How SOLID principles are used in Spring Boot?

Answer:

Principle	Spring Boot Example
SRP	Controller, Service, Repository separation
OCP	Interface + multiple implementations
LSP	Any implementation injected via interface
ISP	Small focused interfaces
DIP	Dependency Injection using @Autowired
Real Microservice Example (Like Your Incentives System)

In your Incentives application, a similar pattern exists:

Controller
   ↓
IncentivesService
   ↓
RewardStrategy (Interface)
   ↓
---------------------------
|                         |
DigitalReward        ExternalReward

we can add new reward type without modifying service code.

Senior Developer Tip ⭐

SOLID + Spring Boot patterns:

Strategy Pattern
Factory Pattern
Dependency Injection

Below possible Question?

1️⃣ Where does Spring Boot use SOLID internally?
2️⃣ How SOLID improves Microservice Architecture?
3️⃣ Refactor a bad code example using SOLID (very commonly asked).

===========================================

