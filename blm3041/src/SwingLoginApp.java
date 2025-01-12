
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

// User class to store logged-in user details
class User {
    private int id;
    private String username;
    private String fname;
    private String lname;
    private String address;

    public User(int id, String username, String fname, String lname, String address) {
        this.id = id;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }
}

public class SwingLoginApp {
    // Database connection details
    static final String DB_URL = "jdbc:postgresql://localhost/blm3041";
    static final String DB_USER = "postgres";
    static final String DB_PASSWORD = "kali";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

// Login Frame
class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        setTitle("Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        messageLabel = new JLabel("", SwingConstants.CENTER);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty placeholder
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
            User user = validateCredentials(conn, username, password);

            if (user != null) {
                messageLabel.setText("Login successful!");
                new HomepageFrame(user).setVisible(true);;
                dispose();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Database error.");
        }
    }

    private User validateCredentials(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM person WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("address")
                );
            }
        }
        return null;
    }
}

// Homepage Frame
class HomepageFrame extends JFrame {
    private User user;

        public HomepageFrame(User user) {
            this.user = user;

            setTitle("Personal Homepage");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));

            JButton accountInfoButton = new JButton("View Account Information");
            JButton viewPetsButton = new JButton("View My Pets");
            JButton vetReservationButton = new JButton("Make a Veterinary Reservation");
            JButton petStoreButton = new JButton("Go to Pet Store");
            JButton logoutButton = new JButton("Logout");

            accountInfoButton.addActionListener(e -> viewAccountInfo());
            viewPetsButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new PetsFrame(user).setVisible(true);
            });
            vetReservationButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new ReservationsFrame(user).setVisible(true);
            });
            petStoreButton.addActionListener(e -> {
                dispose(); // Close the HomepageFrame
                new PetStoreFrame(user).setVisible(true);
            });
            logoutButton.addActionListener(e -> logout());

            buttonPanel.add(accountInfoButton);
            buttonPanel.add(viewPetsButton);
            buttonPanel.add(vetReservationButton);
            buttonPanel.add(petStoreButton);
            buttonPanel.add(logoutButton);

            add(buttonPanel, BorderLayout.CENTER);


        }


        private void logout() {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                dispose(); // Close the HomepageFrame
                new LoginFrame().setVisible(true); // Redirect to login screen
            }
        }
    


    private void viewAccountInfo() {
        JOptionPane.showMessageDialog(this,
                "Full Name: " + user.getFname() + " " + user.getLname() +
                        "\nAddress: " + user.getAddress() +
                        "\nUsername: " + user.getUsername(),
                "Account Information", JOptionPane.INFORMATION_MESSAGE);
    }


    class PetStoreFrame extends JFrame {
        private User user;
        private Cart cart;

        public PetStoreFrame(User user) {
            this.user = user;
            this.cart = new Cart();

            setTitle("Pet Store");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(600, 500);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            // Product list
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new GridLayout(0, 2, 10, 10));

            // Add products dynamically
            String[] products = {
                "Dog Food", "Cat Food", "Bird Cages", "Fish Tanks",
                "Dog Toys", "Cat Litter", "Bird Seeds", "Aquarium Decorations"
            };
            double[] prices = {20.0, 15.0, 40.0, 50.0, 10.0, 8.0, 5.0, 12.0};

            for (int i = 0; i < products.length; i++) {
                String productName = products[i];
                double price = prices[i];

                JPanel productItem = new JPanel(new FlowLayout());
                JLabel productLabel = new JLabel(productName + " ($" + price + ")");
                JTextField quantityField = new JTextField(5);
                JButton addButton = new JButton("Add to Cart");

                addButton.addActionListener(e -> {
                    try {
                        int quantity = Integer.parseInt(quantityField.getText());
                        if (quantity > 0) {
                            cart.addProduct(productName, price, quantity);
                            JOptionPane.showMessageDialog(this, quantity + " " + productName + " added to cart.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number.");
                    }
                });

                productItem.add(productLabel);
                productItem.add(quantityField);
                productItem.add(addButton);
                productPanel.add(productItem);
            }

            JScrollPane productScrollPane = new JScrollPane(productPanel);
            mainPanel.add(productScrollPane, BorderLayout.CENTER);

            // Bottom buttons
            JPanel buttonPanel = new JPanel();
            JButton viewCartButton = new JButton("View Cart");
            JButton goBackButton = new JButton("Go Back");

            viewCartButton.addActionListener(e -> { 
            	dispose();
            	new CartFrame(user, cart).setVisible(true);
            });
            goBackButton.addActionListener(e -> goBack());

            buttonPanel.add(viewCartButton);
            buttonPanel.add(goBackButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);

        }

        private void goBack() {
            new HomepageFrame(user).setVisible(true);;
            dispose();
        }
    }

    class Cart {
        private Map<String, CartItem> cartItems;

        public Cart() {
            cartItems = new HashMap<>();
        }

        public void addProduct(String productName, double price, int quantity) {
            cartItems.putIfAbsent(productName, new CartItem(productName, price, 0));
            cartItems.get(productName).addQuantity(quantity);
        }

        public void removeProduct(String productName) {
            cartItems.remove(productName);
        }

        public void clearCart() {
            cartItems.clear();
        }

        public double getTotalPrice() {
            return cartItems.values().stream().mapToDouble(CartItem::getTotalPrice).sum();
        }

        public Map<String, CartItem> getCartItems() {
            return cartItems;
        }
    }

    class CartItem {
        private String productName;
        private double price;
        private int quantity;

        public CartItem(String productName, double price, int quantity) {
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public void addQuantity(int quantity) {
            this.quantity += quantity;
        }

        public double getTotalPrice() {
            return price * quantity;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    class CartFrame extends JFrame {
        private User user;
        private Cart cart;

        public CartFrame(User user, Cart cart) {
            this.user = user;
            this.cart = cart;

            setTitle("Shopping Cart");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(600, 400);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(new BorderLayout());
            JTextArea cartDetails = new JTextArea();
            cartDetails.setEditable(false);

            // Display cart details
            StringBuilder cartInfo = new StringBuilder("Your Cart:\n");
            for (CartItem item : cart.getCartItems().values()) {
                cartInfo.append(item.getProductName())
                        .append(" - Quantity: ")
                        .append(item.getQuantity())
                        .append(" - Total: $")
                        .append(item.getTotalPrice())
                        .append("\n");
            }

            cartInfo.append("\nTotal Price: $").append(cart.getTotalPrice());
            cartDetails.setText(cartInfo.toString());
            mainPanel.add(new JScrollPane(cartDetails), BorderLayout.CENTER);

            // Bottom buttons
            JPanel buttonPanel = new JPanel();
            JButton removeButton = new JButton("Remove Product");
            JButton purchaseButton = new JButton("Purchase");
            JButton goBackButton = new JButton("Go Back");

            removeButton.addActionListener(e -> {
                String productName = JOptionPane.showInputDialog(this, "Enter product name to remove:");
                if (cart.getCartItems().containsKey(productName)) {
                    cart.removeProduct(productName);
                    JOptionPane.showMessageDialog(this, productName + " removed from cart.");
                    dispose();
                    new CartFrame(user, cart).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found in cart.");
                }
            });

            purchaseButton.addActionListener(e -> {
                if (cart.getCartItems().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Your cart is empty!");
                } else {
                    JOptionPane.showMessageDialog(this, "Purchase successful! Total: $" + cart.getTotalPrice());
                    cart.clearCart();
                    dispose();
                    new CartFrame(user, cart).setVisible(true);
                }
            });

            goBackButton.addActionListener(e -> {
                new PetStoreFrame(user).setVisible(true);
                dispose();
            });

            buttonPanel.add(removeButton);
            buttonPanel.add(purchaseButton);
            buttonPanel.add(goBackButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);

        }
    }


}

//Updated PetsFrame with "Go Back" button and ReservationsFrame
class PetsFrame extends JFrame {
 private User user;

 public PetsFrame(User user) {
     this.user = user;

     setTitle("My Pets");
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setSize(500, 400);
     setLocationRelativeTo(null);

     JPanel buttonPanel = new JPanel();
     buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));

     JButton viewPetsButton = new JButton("View Pets");
     JButton addPetButton = new JButton("Add New Pet");
     JButton viewPetDetailsButton = new JButton("View Pet Details");
     JButton goBackButton = new JButton("Go Back");

     viewPetsButton.addActionListener(e -> viewPets());
     addPetButton.addActionListener(e -> addPet());
     viewPetDetailsButton.addActionListener(e -> viewPetDetails());
     goBackButton.addActionListener(e -> goBack());

     buttonPanel.add(viewPetsButton);
     buttonPanel.add(addPetButton);
     buttonPanel.add(viewPetDetailsButton);
     buttonPanel.add(goBackButton);

     add(buttonPanel, BorderLayout.CENTER);

 }

 private void viewPets() {
     // View pets implementation
     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "SELECT * FROM pet WHERE owner_id = ?";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, user.getId());
             ResultSet rs = stmt.executeQuery();

             StringBuilder petsInfo = new StringBuilder("Your Pets:\n");
             while (rs.next()) {
                 petsInfo.append("-- ID: ").append(rs.getInt("id")).append(", ");
                 petsInfo.append("Name: ").append(rs.getString("name")).append(", ");
                 petsInfo.append("Species: ").append(rs.getString("species")).append(", ");
                 petsInfo.append("Breed: ").append(rs.getString("breed")).append(", ");
                 petsInfo.append("Age: ").append(rs.getInt("age")).append(", ");
                 petsInfo.append("Sex: ").append(rs.getString("sex")).append("\n");
             }

             if (petsInfo.length() == 10) {
                 petsInfo.append("You have no pets.");
             }

             JOptionPane.showMessageDialog(this, petsInfo.toString(), "My Pets", JOptionPane.INFORMATION_MESSAGE);
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error fetching pets.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void addPet() {
     // Add pet implementation
     int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter pet id:"));
     String name = JOptionPane.showInputDialog(this, "Enter pet name:");
     String species = JOptionPane.showInputDialog(this, "Enter pet species:");
     String breed = JOptionPane.showInputDialog(this, "Enter pet breed:");
     int age = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter pet age:"));
     String sex = JOptionPane.showInputDialog(this, "Enter pet sex (M or F):");

     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "INSERT INTO pet (id, name, owner_id, species, breed, age, sex) VALUES (?, ?, ?, ?, ?, ?, ?)";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, id);
             stmt.setString(2, name);
             stmt.setInt(3, user.getId());
             stmt.setString(4, species);
             stmt.setString(5, breed);
             stmt.setInt(6, age);
             stmt.setString(7, sex);
             stmt.executeUpdate();

             JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success",
                     JOptionPane.INFORMATION_MESSAGE);
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error adding pet.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void viewPetDetails() {
	    // Create a new JFrame for displaying pet vaccination and check-up details
	    JFrame petDetailsFrame = new JFrame("Pet Details");
	    petDetailsFrame.setSize(500, 400);
	    petDetailsFrame.setLocationRelativeTo(this);
	    petDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());

	    JComboBox<String> petComboBox = new JComboBox<>();
	    JButton showDetailsButton = new JButton("Show Details");

	    // Populate the JComboBox with the user's pets
	    try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
	        String query = "SELECT id, name FROM pet WHERE owner_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, user.getId()); // Get only pets belonging to the logged-in user
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                petComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
	            }
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error fetching pets.", "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    // If no pets are found, show a message and close the frame
	    if (petComboBox.getItemCount() == 0) {
	        JOptionPane.showMessageDialog(this, "No pets found for this user.", "No Pets", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }

	    panel.add(new JLabel("Select Pet:"), BorderLayout.NORTH);
	    panel.add(petComboBox, BorderLayout.CENTER);
	    panel.add(showDetailsButton, BorderLayout.SOUTH);

	    petDetailsFrame.add(panel);
	    petDetailsFrame.setVisible(true);


	    // Action listener for the "Show Details" button
	    showDetailsButton.addActionListener(e -> {
	        String selectedPet = (String) petComboBox.getSelectedItem();
	        if (selectedPet != null) {
	            int petId = Integer.parseInt(selectedPet.split(" - ")[0]);

	            // Fetch vaccination and check-up details
	            JFrame detailsFrame = new JFrame("Vaccination and Check-Up Details");
	            detailsFrame.setSize(500, 400);
	            detailsFrame.setLocationRelativeTo(petDetailsFrame);
	            detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	            JTextArea detailsArea = new JTextArea();
	            detailsArea.setEditable(false);

	            try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
	                String query = "SELECT vaccination_name, date FROM vaccinations WHERE pet_id = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(query)) {
	                    stmt.setInt(1, petId);
	                    ResultSet rs = stmt.executeQuery();

	                    StringBuilder details = new StringBuilder("Vaccination and Check-Up Details:\n");
	                    while (rs.next()) {
	                        details.append("Vaccination: ").append(rs.getString("vaccination_name")).append(", ");
	                        details.append("Date: ").append(rs.getDate("date")).append("\n");
	                    }

	                    if (details.length() == 33) { // Length of "Vaccination and Check-Up Details:\n"
	                        details.append("No records found.");
	                    }

	                    detailsArea.setText(details.toString());
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(petDetailsFrame, "Error fetching pet details.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            detailsFrame.add(new JScrollPane(detailsArea));
	            detailsFrame.setVisible(true);
	        }
	    });
	}


 private void goBack() {
     // Go back to homepage
     new HomepageFrame(user).setVisible(true);;
     dispose();
 }
}

//ReservationsFrame Class
class ReservationsFrame extends JFrame {
 private User user;

 public ReservationsFrame(User user) {
     this.user = user;

     setTitle("Pet Reservations");
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setSize(500, 400);
     setLocationRelativeTo(null);

     JPanel buttonPanel = new JPanel();
     buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

     JButton makeReservationButton = new JButton("Make Reservation");
     JButton viewReservationsButton = new JButton("View Reservations");
     JButton goBackButton = new JButton("Go Back");

     makeReservationButton.addActionListener(e -> openMakeReservationScreen());
     viewReservationsButton.addActionListener(e -> viewReservations());
     goBackButton.addActionListener(e -> goBack());

     buttonPanel.add(makeReservationButton);
     buttonPanel.add(viewReservationsButton);
     buttonPanel.add(goBackButton);

     add(buttonPanel, BorderLayout.CENTER);


 }

 private void openMakeReservationScreen() {
     // Open a new screen for making a reservation
     new MakeReservationFrame(user).setVisible(true);;
     dispose(); // Close current frame
 }

 private void viewReservations() {
     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "SELECT * FROM reservations WHERE owner_id = ?";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, user.getId());
             ResultSet rs = stmt.executeQuery();

             StringBuilder reservationsInfo = new StringBuilder("Your Reservations:\n");
             while (rs.next()) {
                 reservationsInfo.append("-- Reservation ID: ").append(rs.getInt("id")).append(", ");
                 reservationsInfo.append("Pet ID: ").append(rs.getInt("pet_id")).append(", ");
                 reservationsInfo.append("Clinic: ").append(rs.getString("clinic_name")).append(", ");
                 reservationsInfo.append("Date: ").append(rs.getDate("reservation_date")).append("\n");
             }

             if (reservationsInfo.length() == 19) {
                 reservationsInfo.append("You have no reservations.");
             }

             JOptionPane.showMessageDialog(this, reservationsInfo.toString(), "My Reservations",
                     JOptionPane.INFORMATION_MESSAGE);
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error fetching reservations.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void goBack() {
     // Go back to homepage
     new HomepageFrame(user).setVisible(true);;
     dispose();
 }
}

//MakeReservationFrame Class
class MakeReservationFrame extends JFrame {
 private User user;

 public MakeReservationFrame(User user) {
     this.user = user;

     setTitle("Make Reservation");
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setSize(500, 400);
     setLocationRelativeTo(null);

     JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

     JLabel petLabel = new JLabel("Select Pet:");
     JComboBox<String> petComboBox = new JComboBox<>();
     populatePets(petComboBox);

     JLabel clinicLabel = new JLabel("Select Clinic:");
     JComboBox<String> clinicComboBox = new JComboBox<>();
     populateClinics(clinicComboBox);

     JLabel dateLabel = new JLabel("Select Date (YYYY-MM-DD):");
     JTextField dateField = new JTextField();

     JButton makeReservationButton = new JButton("Make Reservation");
     JButton goBackButton = new JButton("Go Back");

     formPanel.add(petLabel);
     formPanel.add(petComboBox);
     formPanel.add(clinicLabel);
     formPanel.add(clinicComboBox);
     formPanel.add(dateLabel);
     formPanel.add(dateField);

     JPanel buttonPanel = new JPanel();
     buttonPanel.add(makeReservationButton);
     buttonPanel.add(goBackButton);

     add(formPanel, BorderLayout.CENTER);
     add(buttonPanel, BorderLayout.SOUTH);

     makeReservationButton.addActionListener(e -> makeReservation(petComboBox, clinicComboBox, dateField));
     goBackButton.addActionListener(e -> goBackToReservations());


 }

 private void populatePets(JComboBox<String> petComboBox) {
     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "SELECT id, name FROM pet WHERE owner_id = ?";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, user.getId());
             ResultSet rs = stmt.executeQuery();
             while (rs.next()) {
                 petComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
             }
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
     }
 }

 private void populateClinics(JComboBox<String> clinicComboBox) {
     // Populate clinics (static example, can be updated to fetch dynamically from the database)
     clinicComboBox.addItem("Happy Pets Clinic");
     clinicComboBox.addItem("Careful Paws Clinic");
     clinicComboBox.addItem("Healthy Tails Clinic");
 }

 private void makeReservation(JComboBox<String> petComboBox, JComboBox<String> clinicComboBox, JTextField dateField) {
     String petInfo = (String) petComboBox.getSelectedItem();
     String clinicName = (String) clinicComboBox.getSelectedItem();
     String reservationDate = dateField.getText();

     if (petInfo == null || clinicName == null || reservationDate.isEmpty()) {
         JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
         return;
     }

     int petId = Integer.parseInt(petInfo.split(" - ")[0]);

     try (Connection conn = DriverManager.getConnection(SwingLoginApp.DB_URL, SwingLoginApp.DB_USER, SwingLoginApp.DB_PASSWORD)) {
         String query = "INSERT INTO reservations (owner_id, pet_id, clinic_name, reservation_date) VALUES (?, ?, ?, ?)";
         try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, user.getId());
             stmt.setInt(2, petId);
             stmt.setString(3, clinicName);
             stmt.setString(4, reservationDate);
             stmt.executeUpdate();
             JOptionPane.showMessageDialog(this, "Reservation made successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
             goBackToReservations();
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error making reservation.", "Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 private void goBackToReservations() {
     new ReservationsFrame(user).setVisible(true);;
     dispose();
 }
}
