import java.util.*;
import java.io.*;
import java.nio.file.*;

public class SupermarketBillingSystem {

    // Queue for customers
    static Queue<String> customerQueue = new LinkedList<>();

    // HashMap for item prices
    static HashMap<String, Integer> priceList = new HashMap<>();

    // Stack for undo operation
    static Stack<String> itemStack = new Stack<>();

    // Store bill history
    static ArrayList<String> billHistory = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "bills.txt";

    // Initialize price list
    static void initializePrices() {
        priceList.put("Rice", 60);
        priceList.put("Milk", 30);
        priceList.put("Bread", 40);
    }

    // Load previous bills from file
    static void loadBills() {
        try {
            File file = new File(FILE_NAME);

            if (file.exists()) {
                billHistory.addAll(
                    Files.readAllLines(file.toPath())
                );
                System.out.println("Previous bills loaded successfully.");
            } else {
                System.out.println("No previous bill records found.");
            }

        } catch (IOException e) {
            System.out.println("Error loading bills: " + e.getMessage());
        }
    }

    // Add customer to queue
    static void addCustomer() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        customerQueue.add(name);

        System.out.println(name +
                " added to billing queue. Position: "
                + customerQueue.size());
    }

    // View customer queue
    static void viewQueue() {
        if (customerQueue.isEmpty()) {
            System.out.println("Billing queue is empty.");
            return;
        }

        System.out.println("\nCustomers in billing queue:");

        int position = 1;

        for (String customer : customerQueue) {
            System.out.println(position + ". " + customer);
            position++;
        }
    }

    // Bill next customer
    static void billCustomer() {

        if (customerQueue.isEmpty()) {
            System.out.println("Queue is empty. Cannot generate bill.");
            return;
        }

        String customer = customerQueue.peek();

        itemStack.clear();

        System.out.println("\nBilling customer: " + customer);
        System.out.println("Available Items: Rice, Milk, Bread");
        System.out.println("Enter item name and quantity.");
        System.out.println("Type Undo to remove last item.");
        System.out.println("Type Done to generate bill.");

        while (true) {

            System.out.print("\nEnter item: ");
            String item = sc.nextLine().trim();

            if (item.equalsIgnoreCase("Done")) {
                break;
            }

            // Undo last scanned item
            if (item.equalsIgnoreCase("Undo")) {

                if (itemStack.isEmpty()) {
                    System.out.println("No item available to undo.");
                } else {
                    String removed = itemStack.pop();
                    System.out.println("Last item " +
                            removed + " removed.");
                }

                continue;
            }

            // Check whether item exists
            String matchedItem = null;

            for (String key : priceList.keySet()) {
                if (key.equalsIgnoreCase(item)) {
                    matchedItem = key;
                    break;
                }
            }

            if (matchedItem == null) {
                System.out.println("Item " + item +
                        " not found in price list.");
                continue;
            }

            // Get quantity
            System.out.print("Enter quantity: ");

            try {
                int quantity = Integer.parseInt(sc.nextLine());

                if (quantity <= 0) {
                    System.out.println("Invalid quantity.");
                    continue;
                }

                // Store item and quantity in stack
                itemStack.push(matchedItem + " x" + quantity);

                System.out.println(matchedItem + " x" +
                        quantity + " added to bill.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid quantity.");
            }
        }

        // Calculate total bill
        int total = 0;

        System.out.println("\n========== CUSTOMER BILL ==========");
        System.out.println("Customer: " + customer);

        StringBuilder bill = new StringBuilder();

        bill.append("Customer: ").append(customer).append(" | ");

        for (String entry : itemStack) {

            String[] parts = entry.split(" x");

            String item = parts[0];
            int quantity = Integer.parseInt(parts[1]);

            int amount = priceList.get(item) * quantity;

            total += amount;

            System.out.println(item + " x" +
                    quantity + " = " + amount);

            bill.append(item).append(" x")
                .append(quantity).append(" = ")
                .append(amount).append(" | ");
        }

        System.out.println("-----------------------------------");
        System.out.println("Total: " + total);
        System.out.println("===================================");

        bill.append("Total: ").append(total);

        // Save bill in memory
        billHistory.add(bill.toString());

        // Remove billed customer from queue
        customerQueue.poll();

        itemStack.clear();

        System.out.println("Bill generated successfully.");
        System.out.println("Queue is now " +
                (customerQueue.isEmpty() ? "empty." : "not empty."));
    }

    // Save all bills to file
    static void saveBills() {

        try {
            Files.write(
                Paths.get(FILE_NAME),
                billHistory,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("All bills saved to " + FILE_NAME);

        } catch (IOException e) {
            System.out.println("Error saving bills: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {

        initializePrices();

        loadBills();

        while (true) {

            System.out.println("\n===== SUPERMARKET BILLING SYSTEM =====");
            System.out.println("1. Add Customer");
            System.out.println("2. Bill Next Customer");
            System.out.println("3. View Queue");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    addCustomer();
                    break;

                case "2":
                    billCustomer();
                    break;

                case "3":
                    viewQueue();
                    break;

                case "4":
                    saveBills();
                    System.out.println("Thank you for using Supermarket Billing System!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid menu option. Try again.");
            }
        }
    }
}